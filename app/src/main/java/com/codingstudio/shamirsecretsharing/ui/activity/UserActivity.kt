package com.codingstudio.shamirsecretsharing.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.GeneratedKey
import com.codingstudio.shamirsecretsharing.model.Resource
import com.codingstudio.shamirsecretsharing.ui.activity.mqtt.MQTTClient
import com.codingstudio.shamirsecretsharing.ui.viewmodel.SecretSharingViewModel
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.codingstudio.shamirsecretsharing.utils.SharedPref
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.activity_user.relativeLayoutParent
import kotlinx.android.synthetic.main.activity_user.relativeLayoutProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*

class UserActivity : AppCompatActivity() {

    private val TAG = "UserActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var userUniqueId: String = ""
    private var generatedKey: List<GeneratedKey> ?= null
    private lateinit var mqttClient : MQTTClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)

        getDeviceId()
        getUserRequestInfo()
        setOnclickListeners()
        observe()
        mqttConnection()

    }

    private fun setOnclickListeners() {

        relativeLayoutBulb.setOnClickListener {

            viewModel.insertUserRequest(device_id = userUniqueId)

        }

        imageViewNotification.setOnClickListener {

            startActivity(Intent(this, UserActivity3::class.java))

        }

        textViewSendToDevice.setOnClickListener {

            viewModel.sendUserToDevice(device_id = userUniqueId)

        }

        textViewRefresh.setOnClickListener {

            viewModel.getAllConfirmation()
        }

        textViewRequestUser1.setOnClickListener {

            generatedKey?.get(0)?.let { it1 -> it1.generated_key_id?.let { it2 ->
                viewModel.userRequestForKey(
                    it2
                )
            } }

            linearLayoutUserRequest1.visibility = GONE
            linearLayoutUserStatus1.visibility = VISIBLE
        }

        textViewRequestUser2.setOnClickListener {

            generatedKey?.get(1)?.let { it1 -> it1.generated_key_id?.let { it2 ->
                viewModel.userRequestForKey(
                    it2
                )
            } }

            linearLayoutUserRequest2.visibility = GONE
            linearLayoutUserStatus2.visibility = VISIBLE
        }


        bottomBar.onItemSelected = {

            if (it == 2) {
                startActivity(Intent(this, UserActivity3::class.java))
            } else if (it == 1) {
                onBackPressed()
            }

        }

        bottomBar.onItemReselected = {
           // status.text = "Item $it re-selected"
        }

    }

    private fun getUserRequestInfo() {

        viewModel.checkUserRequest(userUniqueId)

        viewModel.getAllConfirmation()

        /*CoroutineScope(Dispatchers.IO).launch {

            while (true) {
                delay(10000)

                viewModel.

            }

        }*/

    }

    private fun observe() {

        viewModel.userRequest.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                val user = userResponseInsertion.data[0]

                                if (user.is_key_generated == "0") {

                                    textViewRequestText.visibility = VISIBLE
                                    textViewRefresh.visibility = VISIBLE
                                    linearLayoutActions.visibility = GONE
                                    linearLayoutUserInfo.visibility = VISIBLE
                                    textViewSendToDevice.visibility = GONE

                                } else {
                                    textViewRequestText.text = "Keys are successfully sent to Device and verified."
                                    textViewRequestText.visibility = VISIBLE
                                    linearLayoutActions.visibility = GONE
                                    linearLayoutUserInfo.visibility = VISIBLE
                                    textViewSendToDevice.visibility = GONE
                                    textViewRefresh.visibility = GONE
                                }

                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = View.GONE

                        linearLayoutUserInfo.visibility = GONE
                        linearLayoutActions.visibility = VISIBLE
                        textViewSendToDevice.visibility = GONE
                        textViewRefresh.visibility = GONE

                        response.message?.let { errorMessage ->
                            Log.e(TAG, "An Error Occurred : $errorMessage")
                            when (errorMessage) {

                                Constant.NO_INTERNET -> {
                                    Snackbar.make(relativeLayoutParent, "No internet connection", Snackbar.LENGTH_LONG).show()
                                }
                                Constant.CONFLICT -> {
                                    //Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    //Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
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

        viewModel.insertUserRequest.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                textViewRequestText.visibility = VISIBLE
                                textViewRefresh.visibility = VISIBLE
                                linearLayoutActions.visibility = GONE
                                textViewSendToDevice.visibility = GONE
                                linearLayoutUserInfo.visibility = VISIBLE

                            } else {
                                Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = View.GONE
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
                        relativeLayoutProgressBar.visibility = View.VISIBLE
                    }

                }

            }
        })

        viewModel.sendToDevice.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                textViewRequestText.text = "Keys are successfully sent to Device and verified."
                                textViewRequestText.visibility = VISIBLE
                                linearLayoutActions.visibility = GONE
                                textViewSendToDevice.visibility = GONE
                                textViewRefresh.visibility = GONE
                                textViewUserResponse1.visibility = GONE
                                textViewUserResponse2.visibility = GONE
                                linearLayoutUserInfo.visibility = VISIBLE

                                publishMessageToMqtt()

                            } else {
                                Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = View.GONE
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
                        relativeLayoutProgressBar.visibility = View.VISIBLE
                    }

                }

            }
        })

        viewModel.getAllConfirmations.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                textViewRequestText.visibility = VISIBLE
                                linearLayoutActions.visibility = GONE
                                textViewSendToDevice.visibility = GONE
                                linearLayoutUserInfo.visibility = VISIBLE
                                textViewRefresh.visibility = VISIBLE

                                val generatedKeys = userResponseInsertion.data
                                generatedKey = userResponseInsertion.data

                                try {

                                    if (generatedKeys[0].is_requested == "1") {
                                        linearLayoutUserStatus1.visibility = VISIBLE
                                        linearLayoutUserRequest1.visibility = GONE

                                    } else {
                                        linearLayoutUserStatus1.visibility = GONE
                                        linearLayoutUserRequest1.visibility = VISIBLE
                                    }

                                    if (generatedKeys[1].is_requested == "1") {
                                        linearLayoutUserStatus2.visibility = VISIBLE
                                        linearLayoutUserRequest2.visibility = GONE

                                    } else {
                                        linearLayoutUserStatus2.visibility = GONE
                                        linearLayoutUserRequest2.visibility = VISIBLE
                                    }

                                    if (generatedKeys[0].is_active == "1") {
                                        textViewUserResponse1.text = "Confirmed"
                                        textViewUserResponse1.setTextColor(ContextCompat.getColor(this, R.color.green))
                                        imageViewUserResponse1.setBackgroundColor(ContextCompat.getColor(this, R.color.green))

                                    } else {
                                        textViewUserResponse1.text = "Not Confirmed"
                                        textViewUserResponse1.setTextColor(ContextCompat.getColor(this, R.color.red))
                                        imageViewUserResponse1.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                                    }

                                    if (generatedKeys[1].is_active == "1") {
                                        textViewUserResponse2.text = "Confirmed"
                                        textViewUserResponse2.setTextColor(ContextCompat.getColor(this, R.color.green))
                                        imageViewUserResponse2.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                                    } else {
                                        textViewUserResponse2.text = "Not Confirmed"
                                        textViewUserResponse2.setTextColor(ContextCompat.getColor(this, R.color.red))
                                        imageViewUserResponse2.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                                    }

                                    if (generatedKeys[0].is_active == "1" && generatedKeys[1].is_active == "1") {
                                        textViewSendToDevice.visibility = VISIBLE
                                        textViewRefresh.visibility = GONE
                                        textViewRequestText.text = "All the users are confirmed the request. You can now send the keys to the Device by clicking Sent to Device button."

                                    } else {
                                        textViewSendToDevice.visibility = GONE
                                    }

                                } catch (ex: Exception) {
                                    Snackbar.make(relativeLayoutParent, "$ex", Snackbar.LENGTH_LONG).show()
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
                                    Snackbar.make(relativeLayoutParent, "No internet connection", Snackbar.LENGTH_LONG).show()
                                }
                                Constant.CONFLICT -> {
                                    //Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    //Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
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

        viewModel.updateKeyReuestForUser.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when(response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                Snackbar.make(relativeLayoutParent, "Successfully Requested for Keys.", Snackbar.LENGTH_LONG).show()

                            }
                        }
                    }
                    is Resource.Error -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.message?.let { errorMessage ->
                            Log.e(TAG, "An Error Occurred : $errorMessage")
                            when (errorMessage) {

                                Constant.NO_INTERNET -> {
                                    Snackbar.make(relativeLayoutParent, "No internet connection", Snackbar.LENGTH_LONG).show()
                                }
                                Constant.CONFLICT -> {
                                    //Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    //Snackbar.make(relativeLayoutParent, "Something went wrong. Please try again", Snackbar.LENGTH_LONG).show()
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

    private fun mqttConnection() {

        val serverURI = "tcp://broker.emqx.io:1883"
        val clientId    = ""
        val username    = ""
        val pwd         = ""


        // Open MQTT Broker communication
        mqttClient = MQTTClient(this, serverURI, clientId)



        // Connect and login to MQTT Broker
        mqttClient.connect( username,
            pwd,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Connection success")

                    Toast.makeText(this@UserActivity, "MQTT Connection success", Toast.LENGTH_SHORT).show()

                    //publishMessageToMqtt()

                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(this.javaClass.name, "Connection failure: ${exception.toString()}")

                    Toast.makeText(this@UserActivity, "MQTT Connection fails: ${exception.toString()}", Toast.LENGTH_SHORT).show()

                    // Come back to Connect Fragment
                    //findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Receive message: ${message.toString()} from topic: $topic"
                    Log.d(this.javaClass.name, msg)

                    Toast.makeText(this@UserActivity, msg, Toast.LENGTH_SHORT).show()
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(this.javaClass.name, "Delivery complete")
                }
            })

    }

    private fun publishMessageToMqtt() {

        val topic   = "led"
        val message = "user,${generatedKey?.get(0)?.hash_key},${generatedKey?.get(1)?.hash_key}"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg ="Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)

                        Toast.makeText(this@UserActivity, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to publish message to topic")
                    }
                })

        } else {
            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (mqttClient.isConnected()) {
            // Disconnect from MQTT Broker
            mqttClient.disconnect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Disconnected")

                    Toast.makeText(this@UserActivity, "MQTT Disconnection success", Toast.LENGTH_SHORT).show()

                    // Disconnection success, come back to Connect Fragment
                    //findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(this.javaClass.name, "Failed to disconnect")
                }
            })
        } else {
            Log.d(this.javaClass.name, "Impossible to disconnect, no server connected")
        }

    }



}