package com.codingstudio.shamirsecretsharing.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.Resource
import com.codingstudio.shamirsecretsharing.ui.viewmodel.SecretSharingViewModel
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.codingstudio.shamirsecretsharing.utils.SharedPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var fcmToken: String? = ""
    private var userUniqueId: String = ""
    private var btnClicked = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)

        val userType = SharedPref().getStringPref(this, Constant.USER_TYPE)

        if (userType == "ADMIN" ) {
            startActivity(Intent(this, AdminActivity::class.java))

        } else if (userType == "USER" ) {
            startActivity(Intent(this, UserActivity::class.java))

        }

        setOnclickListeners()
        generateFCMToken()
        getDeviceId()
        observe()
    }

    private fun setOnclickListeners() {

        textViewAdmin.setOnClickListener {

            viewModel.getUserInfo(
                user_name = "username",
                device_id = userUniqueId,
                fcm = fcmToken ?: "",
                type = "ADMIN"
            )

            btnClicked = "ADMIN"

        }

        textViewUser.setOnClickListener {

            viewModel.getUserInfo(
                user_name = "username",
                device_id = userUniqueId,
                fcm = fcmToken ?: "",
                type = "USER"
            )

            btnClicked = "USER"

        }

        textViewDevice.setOnClickListener {

            viewModel.getUserInfo(
                user_name = "username",
                device_id = userUniqueId,
                fcm = fcmToken ?: "",
                type = "DEVICE"
            )

            btnClicked = "DEVICE"

        }

        textViewClearRequest.setOnClickListener {
            viewModel.deleteRequest()
        }

        textViewClearAll.setOnClickListener {
            viewModel.deleteAllData()
        }

    }

    private fun observe() {

        viewModel.userInfo.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                if (userResponseInsertion.message == "FIRST") {

                                    if (btnClicked == "ADMIN") {

                                        SharedPref().setString(this, Constant.USER_TYPE, "ADMIN")
                                        startActivity(Intent(this, AdminActivity::class.java))

                                    } else if (btnClicked == "USER") {
                                        SharedPref().setString(this, Constant.USER_TYPE, "USER")
                                        startActivity(Intent(this, UserActivity::class.java))

                                    } else if (btnClicked == "DEVICE") {
                                        SharedPref().setString(this, Constant.USER_TYPE, "DEVICE")
                                        startActivity(Intent(this, DeviceActivity::class.java))

                                    }

                                } else if (userResponseInsertion.message == "SECOND") {

                                    Snackbar.make(relativeLayoutParent, "Your device is registered as different type of user. Please try another type or clear data from database.", Snackbar.LENGTH_LONG).show()


                                } else if (userResponseInsertion.message == "THIRD") {

                                    if (btnClicked == "ADMIN") {

                                        SharedPref().setString(this, Constant.USER_TYPE, "ADMIN")
                                        Snackbar.make(relativeLayoutParent, "Your login is created as Admin", Snackbar.LENGTH_LONG).show()
                                        startActivity(Intent(this, AdminActivity::class.java))

                                    } else if (btnClicked == "USER") {

                                        SharedPref().setString(this, Constant.USER_TYPE, "USER")
                                        Snackbar.make(relativeLayoutParent, "Your login is created as User", Snackbar.LENGTH_LONG).show()
                                        startActivity(Intent(this, UserActivity::class.java))

                                    } else if (btnClicked == "DEVICE") {

                                        SharedPref().setString(this, Constant.USER_TYPE, "DEVICE")
                                        Snackbar.make(relativeLayoutParent, "Your login is created as Device", Snackbar.LENGTH_LONG).show()
                                        startActivity(Intent(this, DeviceActivity::class.java))

                                    }

                                } else {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }

                            } else {
                                Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = GONE
                        response.message?.let { errorMessage ->
                            Log.e(TAG, "An Error Occurred : $errorMessage")
                            when (errorMessage) {

                                Constant.NO_INTERNET -> {
                                    Snackbar.make(relativeLayoutParent, "No internet connection", Snackbar.LENGTH_LONG).show()
                                }
                                Constant.CONFLICT -> {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {
                        relativeLayoutProgressBar.visibility = VISIBLE
                    }

                }

            }
        })

        viewModel.deleteRequest.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                Snackbar.make(relativeLayoutParent, "Successfully Deleted Request Data", Snackbar.LENGTH_LONG).show()

                            } else {
                                Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = GONE
                        response.message?.let { errorMessage ->
                            Log.e(TAG, "An Error Occurred : $errorMessage")
                            when (errorMessage) {

                                Constant.NO_INTERNET -> {
                                    Snackbar.make(relativeLayoutParent, "No internet connection", Snackbar.LENGTH_LONG).show()
                                }
                                Constant.CONFLICT -> {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {
                        relativeLayoutProgressBar.visibility = VISIBLE
                    }

                }

            }
        })

        viewModel.deleteAllData.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                Snackbar.make(relativeLayoutParent, "Successfully Deleted All User Data", Snackbar.LENGTH_LONG).show()
                                SharedPref().setString(this, Constant.USER_TYPE, "")

                            } else {
                                Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = GONE
                        response.message?.let { errorMessage ->
                            Log.e(TAG, "An Error Occurred : $errorMessage")
                            when (errorMessage) {

                                Constant.NO_INTERNET -> {
                                    Snackbar.make(relativeLayoutParent, "No internet connection", Snackbar.LENGTH_LONG).show()
                                }
                                Constant.CONFLICT -> {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {
                        relativeLayoutProgressBar.visibility = VISIBLE
                    }

                }

            }
        })


    }

    private fun generateFCMToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            fcmToken = token
            Log.e(TAG, "$token")
        })
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId() {

        try {
            userUniqueId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        }catch (ex: Exception) {
            Log.e(TAG, "getDeviceId: $ex" )
            ///Firebase.crashlytics.recordException(ex)
        }


    }



}