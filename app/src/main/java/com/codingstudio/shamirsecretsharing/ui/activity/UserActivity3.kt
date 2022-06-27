package com.codingstudio.shamirsecretsharing.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.Resource
import com.codingstudio.shamirsecretsharing.ui.activity.mqtt.MQTTClient
import com.codingstudio.shamirsecretsharing.ui.viewmodel.SecretSharingViewModel
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_user3.*
import org.eclipse.paho.client.mqttv3.*

class UserActivity3 : AppCompatActivity() {

    private val TAG = "UserActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var userUniqueId: String = ""
    private lateinit var mqttClient : MQTTClient
    private var key1 = ""
    private var key2 = ""
    private var key3 = ""
    private var userID = ""
    private var deviceName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user3)

        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)


        textViewUserConfirm.setOnClickListener {
            //viewModel.confirmAccess(userUniqueId)

            publishMessageForUserRequest()
        }

        textViewUserReject.setOnClickListener {

            publishMessageForUserRequestReject()
        }

        getDeviceId()
        //getUserRequestInfo()
        //observe()

        bottomBar.itemActiveIndex = 2

        bottomBar.onItemSelected = {

            if (it == 1) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else if (it == 0) {
                onBackPressed()
            }

        }

        mqttConnection()

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

    private fun mqttConnection() {

        val serverURI = "tcp://broker.emqx.io:1883"
        val clientId    = ""
        val username    = ""
        val pwd         = ""


        // Open MQTT Broker communication
        mqttClient = MQTTClient(this, serverURI, clientId)


        // Connect and login to MQTT Broker
        mqttClient.connect(username,
            pwd,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")

                    Toast.makeText(this@UserActivity3, "MQTT Connection success", Toast.LENGTH_SHORT)
                        .show()

                    subscribeUser()

                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure: ${exception.toString()}")

                    Toast.makeText(
                        this@UserActivity3,
                        "MQTT Connection fails: ${exception.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Come back to Connect Fragment
                    //findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Receive message: ${message.toString()} from topic: $topic"
                    Log.d(TAG, msg)

                    Toast.makeText(this@UserActivity3, msg, Toast.LENGTH_SHORT).show()

                    val keys = message.toString().split(",")

                    if (topic == "user" && message.toString() == userUniqueId) {

                        textViewUserMessage.visibility = View.VISIBLE
                        textViewUserConfirm.visibility = View.VISIBLE
                        textViewUserReject.visibility = View.VISIBLE
                        textViewUserMessage.text = "There is one request from $userID for the device $deviceName. Kindly press Confirm button to give access to the request from your side."

                    } else if (topic == "user" && keys[0] == "key_generated") {

                        try {
                            key1 = keys[1]
                            key2 = keys[2]
                            key3 = keys[3]
                            userID = keys[4]
                            deviceName = keys[5]

                            textViewUserMessage.text = "Admin has processed the request for the device $deviceName received from $userID."
                            textViewUserMessage.setTextColor(ContextCompat.getColor(this@UserActivity3, R.color.green))

                        } catch (e: Exception){
                            Toast.makeText(this@UserActivity3, "Users not found. Please register user first.", Toast.LENGTH_LONG).show()
                        }

                    }

                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(TAG, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(TAG, "Delivery complete")
                }
            })

    }

    private fun subscribeUser() {

        val topic = "user"

        if (mqttClient.isConnected()) {
            mqttClient.subscribe(topic,
                1,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Subscribed to: $topic"
                        Log.d(TAG, msg)

                        Toast.makeText(this@UserActivity3, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Failed to subscribe: $topic")
                    }
                })
        } else {
            Log.d(TAG, "Impossible to subscribe, no server connected")
        }

    }

    private fun publishMessageForUserRequest() {

        val topic   = "user"
        val message = "$userUniqueId,request_accepted"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)
                        Toast.makeText(this@UserActivity3, "Successfully Confirmed Request", Toast.LENGTH_SHORT).show()

                        textViewUserMessage.visibility = View.VISIBLE
                        textViewUserConfirm.visibility = View.GONE
                        textViewUserReject.visibility = View.GONE
                        textViewUserMessage.text = "Successfully confirmed the request."

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to publish message to topic")
                    }
                })

        } else {
            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
        }

    }

    private fun publishMessageForUserRequestReject() {

        val topic   = "user"
        val message = "$userUniqueId,request_rejected"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)
                        Toast.makeText(this@UserActivity3, "Successfully Rejected The Request", Toast.LENGTH_SHORT).show()

                        textViewUserMessage.visibility = View.VISIBLE
                        textViewUserConfirm.visibility = View.GONE
                        textViewUserReject.visibility = View.GONE
                        textViewUserMessage.text = "Successfully rejected the request."

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to publish message to topic")
                    }
                })

        } else {
            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
        }

    }


}