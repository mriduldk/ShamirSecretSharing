package com.codingstudio.shamirsecretsharing.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.Resource
import com.codingstudio.shamirsecretsharing.ui.viewmodel.SecretSharingViewModel
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.activity_user3.*
import kotlinx.android.synthetic.main.activity_user3.bottomBar
import kotlinx.android.synthetic.main.activity_user3.relativeLayoutParent
import kotlinx.android.synthetic.main.activity_user3.relativeLayoutProgressBar

class UserActivity3 : AppCompatActivity() {

    private val TAG = "UserActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var userUniqueId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user3)

        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)


        textViewUserConfirm.setOnClickListener {
            viewModel.confirmAccess(userUniqueId)
        }

        getDeviceId()
        getUserRequestInfo()
        observe()

        bottomBar.itemActiveIndex = 2

        bottomBar.onItemSelected = {

            if (it == 1) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else if (it == 0) {
                onBackPressed()
            }

        }

    }

    private fun getUserRequestInfo() {

        viewModel.checkUserGeneratedKey(userUniqueId)

    }

    private fun observe() {

        viewModel.checkUserGeneratedKey.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                if (userResponseInsertion.data[0].is_active == "0") {

                                    textViewUserMessage.visibility = View.VISIBLE
                                    textViewUserConfirm.visibility = View.VISIBLE
                                    textViewUserMessage.text = "There is one request from user. Kindly press Confirm button to give access to the request from your side."

                                } else {
                                    textViewUserMessage.visibility = View.VISIBLE
                                    textViewUserConfirm.visibility = View.GONE
                                    textViewUserMessage.text = "Successfully confirmed the request."

                                }

                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = View.GONE
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
                                    Snackbar.make(relativeLayoutParent, "No Request Found", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    Snackbar.make(relativeLayoutParent, "No Request Found", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {
                        relativeLayoutProgressBar.visibility = View.VISIBLE
                    }

                }

            }
        })

        viewModel.confirmAccess.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                textViewUserMessage.visibility = View.VISIBLE
                                textViewUserConfirm.visibility = View.GONE
                                textViewUserMessage.text = "Successfully confirmed the request."
                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = View.GONE
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
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {
                        relativeLayoutProgressBar.visibility = View.VISIBLE
                    }

                }

            }
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