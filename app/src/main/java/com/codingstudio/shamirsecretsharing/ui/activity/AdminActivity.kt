package com.codingstudio.shamirsecretsharing.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.Resource
import com.codingstudio.shamirsecretsharing.model.User
import com.codingstudio.shamirsecretsharing.secretSharing.Scheme
import com.codingstudio.shamirsecretsharing.ui.activity.mqtt.MQTTClient
import com.codingstudio.shamirsecretsharing.ui.viewmodel.SecretSharingViewModel
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_admin.*
import okio.internal.commonAsUtf8ToByteArray
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.StandardCharsets
import java.security.SecureRandom


class AdminActivity : AppCompatActivity() {

    private val TAG = "UserActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var userUniqueId: String = ""
    private var user : User ?= null
    private lateinit var mqttClient : MQTTClient


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)

        textViewAdminProceed.setOnClickListener {

            val scheme = Scheme(SecureRandom(), 2, 2)
            val secret: ByteArray = "hello there".commonAsUtf8ToByteArray()
            val parts = scheme.split(secret)

            val keyArr = ArrayList<String>()

            for (item in parts) {
                keyArr.add(item.value.toString())
            }

            val gson = Gson()
            val keys = gson.toJson(keyArr)

            proceedApiCall(keys)

            publishMessageToMqtt("${keyArr[0]},${keyArr[1]}")

        }

        getUserRequestInfo()
        observe()
        mqttConnection()

    }

    private fun proceedApiCall(keys : String) {

        viewModel.adminProcessed(
            device_id = user?.fk_user_device_id ?: "",
            hash_keys = keys
        )

    }

    private fun getUserRequestInfo() {

        viewModel.checkUserRequestForAdmin()

    }

    private fun observe() {

        viewModel.userRequestAdmin.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                user = userResponseInsertion.data[0]

                                textViewAdminMessage.visibility = View.VISIBLE

                                if (user?.is_processed == "0") {

                                    textViewAdminProceed.visibility = View.VISIBLE
                                    textViewAdminMessage.text = "There is one request from user. Kindly press proceed button to generate key and circulate to another users (2 user)."

                                } else {
                                    textViewAdminProceed.visibility = View.GONE
                                    textViewAdminMessage.text = "Key generated successfully and sent to the users."
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

        viewModel.adminProcessed.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                textViewAdminProceed.visibility = View.GONE
                                textViewAdminMessage.visibility = View.VISIBLE
                                textViewAdminMessage.text = "Key generated successfully and sent to the users."
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
                    Log.d(this.javaClass.name, "Connection success")

                    Toast.makeText(this@AdminActivity, "MQTT Connection success", Toast.LENGTH_SHORT).show()

                    //publishMessageToMqtt(keys)

                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(this.javaClass.name, "Connection failure: ${exception.toString()}")

                    Toast.makeText(this@AdminActivity, "MQTT Connection fails: ${exception.toString()}", Toast.LENGTH_SHORT).show()

                    // Come back to Connect Fragment
                    //findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Receive message: ${message.toString()} from topic: $topic"
                    Log.d(this.javaClass.name, msg)

                    Toast.makeText(this@AdminActivity, msg, Toast.LENGTH_SHORT).show()
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(this.javaClass.name, "Delivery complete")
                }
            })

    }

    private fun publishMessageToMqtt(keys : String) {

        val topic   = "led"
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

}