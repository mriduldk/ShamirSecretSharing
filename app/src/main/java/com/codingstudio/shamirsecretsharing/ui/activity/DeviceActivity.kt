package com.codingstudio.shamirsecretsharing.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.Resource
import com.codingstudio.shamirsecretsharing.ui.viewmodel.SecretSharingViewModel
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_device.*

class DeviceActivity : AppCompatActivity() {

    private val TAG = "DeviceActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var userUniqueId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)


        textViewRefresh.setOnClickListener {
            viewModel.getAllConfirmation()
        }

        getDeviceId()
        getUserRequestInfo()
        observe()
    }

    private fun getUserRequestInfo() {

        viewModel.getAllConfirmation()

    }

    private fun observe() {

        viewModel.getAllConfirmations.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {
                                val generatedKeys = userResponseInsertion.data
                                try {
                                    if (generatedKeys[0].is_active == "1" && generatedKeys[1].is_active == "1") {
                                        textViewMessage.text = "All keys are received and successfully verified."
                                        textViewMessage.setTextColor(ContextCompat.getColor(this, R.color.green))
                                    } else {
                                        textViewMessage.text = "User request not verified yet."
                                        textViewMessage.setTextColor(ContextCompat.getColor(this, R.color.red))
                                    }
                                } catch (ex: Exception) {
                                    Snackbar.make(relativeLayoutParent, "$ex", Snackbar.LENGTH_LONG).show()
                                    textViewMessage.text = "User request not verified yet."
                                    textViewMessage.setTextColor(ContextCompat.getColor(this, R.color.red))
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        textViewMessage.text = "No Request found."

                        response.message?.let { errorMessage ->
                            Log.e(TAG, "An Error Occurred : $errorMessage")
                            when (errorMessage) {

                                Constant.NO_INTERNET -> {
                                    Snackbar.make(relativeLayoutParent, "No internet connection", Snackbar.LENGTH_LONG).show()
                                }
                                Constant.CONFLICT -> {
                                    Snackbar.make(relativeLayoutParent, "No Request found.", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    Snackbar.make(relativeLayoutParent, "No Request found.", Snackbar.LENGTH_LONG).show()
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
        }
    }









    }