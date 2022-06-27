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
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var userUniqueId: String = ""
    private var fcmToken: String? = ""
    private var username: String = ""
    private var password: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)


        generateFCMToken()
        getDeviceId()
        setOnClickListener()
        observe()

    }

    private fun setOnClickListener() {

        textViewLogin.setOnClickListener {

            username = editTextUserEmail.text.toString().trim()
            password = editTextPassword.text.toString().trim()

            viewModel.getUserInfo2(
                user_name = username,
                password = password,
                device_id = userUniqueId,
                fcm = fcmToken ?: "",
                type = "USER"
            )

        }

        imageViewBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun observe() {

        viewModel.userInfo2.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                if (userResponseInsertion.message == "FIRST") {

                                    SharedPref().setString(this, Constant.USER_TYPE, "USER")
                                    SharedPref().setString(this, Constant.USERNAME, username)
                                    startActivity(Intent(this, UserActivity::class.java))
                                    finish()

                                } else if (userResponseInsertion.message == "SECOND") {

                                    Snackbar.make(relativeLayoutParent, "Password is not correct or Your device is registered as different user. Please try to clear data from database.", Snackbar.LENGTH_LONG).show()

                                } else if (userResponseInsertion.message == "THIRD") {

                                    SharedPref().setString(this, Constant.USER_TYPE, "USER")
                                    SharedPref().setString(this, Constant.USERNAME, username)
                                    Snackbar.make(relativeLayoutParent, "Your login is created as User", Snackbar.LENGTH_LONG).show()
                                    startActivity(Intent(this, UserActivity::class.java))
                                    finish()

                                } else {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }

                            } else {
                                Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { errorMessage ->
                            Log.e(TAG, "An Error Occurred : $errorMessage")
                            when (errorMessage) {

                                Constant.NO_INTERNET -> {
                                    Snackbar.make(
                                        relativeLayoutParent,
                                        "No internet connection",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                                Constant.CONFLICT -> {
                                    Snackbar.make(
                                        relativeLayoutParent,
                                        "Something went wrong. Please try again",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                                else -> {
                                    Snackbar.make(
                                        relativeLayoutParent,
                                        "Something went wrong. Please try again",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
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

    private fun showProgressBar() {

        textViewLogin.visibility = GONE
        linearLayoutDisabledButton.visibility = VISIBLE
    }

    private fun hideProgressBar() {

        textViewLogin.visibility = VISIBLE
        linearLayoutDisabledButton.visibility = GONE
    }


}