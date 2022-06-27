package com.codingstudio.shamirsecretsharing.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.Resource
import com.codingstudio.shamirsecretsharing.model.User
import com.codingstudio.shamirsecretsharing.secretSharing.Scheme
import com.codingstudio.shamirsecretsharing.ui.activity.mqtt.MQTTClient
import com.codingstudio.shamirsecretsharing.ui.viewmodel.SecretSharingViewModel
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.codingstudio.shamirsecretsharing.utils.SharedPref
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_admin.imageViewLogout
import kotlinx.android.synthetic.main.activity_admin.relativeLayoutParent
import kotlinx.android.synthetic.main.activity_admin.relativeLayoutProgressBar
import kotlinx.android.synthetic.main.activity_user.*
import okio.internal.commonAsUtf8ToByteArray
import org.eclipse.paho.client.mqttv3.*
import java.security.SecureRandom


class AdminActivity : AppCompatActivity() {

    private val TAG = "AdminActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var userUniqueId: String = ""
    private var user : User ?= null
    private var userList : List<User> ?= null
    private lateinit var mqttClient : MQTTClient

    private var userId = ""
    private var deviceName = ""

    private var messageNoOfUser = ""
    private var noOfUser = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)


        textViewAdminProceed.setOnClickListener {

            if (noOfUser > 0) {

                val scheme = Scheme(SecureRandom(), noOfUser, noOfUser)
                val secret: ByteArray = deviceName.commonAsUtf8ToByteArray()
                val parts = scheme.split(secret)

                val keyArr = ArrayList<String>()

                for (item in parts) {
                    keyArr.add(item.value.toString())
                }

                val gson = Gson()
                val keys = gson.toJson(keyArr)

                //proceedApiCall(keys)

                var keysStr = ""
                keyArr.forEachIndexed { index, key ->

                    if (index != noOfUser - 1) {
                        keysStr = "$keysStr$key,"
                    } else {
                        keysStr = "$keysStr$key"
                    }
                }

                //publishMessageToMqtt("${keyArr[0]},${keyArr[1]},${keyArr[2]}")
                //publishMessageToMqtt2("${keyArr[0]},${keyArr[1]},${keyArr[2]}")
                publishMessageToMqtt(keysStr)
                publishMessageToMqtt2(keysStr)

            } else {
                Snackbar.make(relativeLayoutParent, "No User Found. Please register user.", Snackbar.LENGTH_LONG).show()
            }
        }

        checkAdminInfo()
        observe()
        mqttConnection()

        imageViewLogout.setOnClickListener {

            SharedPref().logoutUser(this)
            finish()
        }

        btnSave.setOnClickListener {
            proceedApiCall()
        }

    }

    private fun proceedApiCall() {

        if (editTextNoOfUser.text.toString().trim().isNullOrEmpty() || editTextPercentage.text.toString().trim().isNullOrEmpty()) {

            Snackbar.make(relativeLayoutParent, "Fill all the fields", Snackbar.LENGTH_LONG).show()
        } else {

            messageNoOfUser = "Lower Bound : ${editTextNoOfUser.text.toString().trim()} \nPercentage of dynamic user for threshold : ${editTextPercentage.text.toString().trim()}"

            viewModel.adminSetUserNo(
                user_no = editTextNoOfUser.text.toString().trim(),
                percentage = editTextPercentage.text.toString().trim()
            )
        }
    }

    private fun checkAdminInfo() {

        viewModel.checkAdminInfo()
        viewModel.getAllUser()

    }

    private fun observe() {

        viewModel.adminInfo.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                val adminInfo = userResponseInsertion.data
                                messageNoOfUser = "Lower Bound : ${adminInfo.no_of_user} \nPercentage of dynamic user for threshold : ${adminInfo.percentage}"

                                linearLayoutNoOfUserAdmin.visibility = View.GONE
                                linearLayoutAdminActions.visibility = View.VISIBLE
                                textViewAdminMessageNoOfUser.text = messageNoOfUser

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

        viewModel.getAllUser.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                userList = userResponseInsertion.data
                                noOfUser = userResponseInsertion.data.size - 1

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

        viewModel.adminUserNo.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                linearLayoutNoOfUserAdmin.visibility = View.GONE
                                linearLayoutAdminActions.visibility = View.VISIBLE
                                textViewAdminMessageNoOfUser.text = messageNoOfUser
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
                                    Snackbar.make(relativeLayoutParent, "Create two user to send keys after generating.", Snackbar.LENGTH_LONG).show()
                                }
                                else -> {
                                    Snackbar.make(relativeLayoutParent, "Create two user to send keys after generating.", Snackbar.LENGTH_LONG).show()
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
                    Log.d(TAG, "Connection success")

                    Toast.makeText(this@AdminActivity, "MQTT Connection success", Toast.LENGTH_SHORT).show()

                    subscribeAdmin()

                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure: ${exception.toString()}")

                    Toast.makeText(this@AdminActivity, "MQTT Connection fails: ${exception.toString()}", Toast.LENGTH_SHORT).show()

                    // Come back to Connect Fragment
                    //findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Receive message: ${message.toString()} from topic: $topic"
                    Log.d(TAG, msg)
                    //Toast.makeText(this@AdminActivity, msg, Toast.LENGTH_SHORT).show()

                    val request = message.toString().split(",")

                    if (topic == "admin" && request[0] == "user_request") {

                        try {
                            userId = request[1]
                            deviceName = request[2]

                            textViewAdminProceed.visibility = View.VISIBLE
                            textViewAdminMessage.text = "There is one request from $userId for the device $deviceName.\n\nKindly press proceed button to generate key and circulate to another users."

                        } catch (e: Exception){
                            Toast.makeText(this@AdminActivity, "Invalid request. Please try again", Toast.LENGTH_LONG).show()
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

    private fun publishMessageToMqtt(keys : String) {

        val topic   = "user"
        val message = "key_generated,$keys,$userId,$deviceName"

        if (mqttClient.isConnected()) {
            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg ="Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)

                        Toast.makeText(this@AdminActivity, msg, Toast.LENGTH_SHORT).show()

                        SharedPref().setString(this@AdminActivity, Constant.ADMIN_STATUS, Constant.ADMIN_STATUS_2)
                        textViewAdminProceed.visibility = View.GONE
                        textViewAdminMessage.text = "Key generated successfully and sent to the users."

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to publish message to topic")
                    }
                })
        } else {
            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
        }

    }

    private fun publishMessageToMqtt2(keys : String) {

        val topic   = "device"
        val message = "admin,$keys"

        if (mqttClient.isConnected()) {
            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg ="Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)

                        Toast.makeText(this@AdminActivity, msg, Toast.LENGTH_SHORT).show()

                        SharedPref().setString(this@AdminActivity, Constant.ADMIN_STATUS, Constant.ADMIN_STATUS_2)
                        textViewAdminProceed.visibility = View.GONE
                        textViewAdminMessage.text = "Key generated successfully and sent to the users."

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

                    Toast.makeText(this@AdminActivity, "MQTT Disconnection success", Toast.LENGTH_SHORT).show()

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

    private fun subscribeAdmin() {

        val topic = "admin"

        if (mqttClient.isConnected()) {
            mqttClient.subscribe(topic,
                1,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Subscribed to: $topic"
                        Log.d(TAG, msg)

                        Toast.makeText(this@AdminActivity, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Failed to subscribe: $topic")
                    }
                })
        } else {
            Log.d(TAG, "Impossible to subscribe, no server connected")
        }

    }

}