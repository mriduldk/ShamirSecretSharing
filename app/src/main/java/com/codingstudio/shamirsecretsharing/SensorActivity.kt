package com.codingstudio.shamirsecretsharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.codingstudio.shamirsecretsharing.ui.activity.mqtt.MQTTClient
import kotlinx.android.synthetic.main.activity_sensor.*
import org.eclipse.paho.client.mqttv3.*

class SensorActivity : AppCompatActivity() {

    private val TAG = "SensorActivity"
    private var userUniqueId: String = ""
    private lateinit var mqttClient : MQTTClient
    private var deviceName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sensor)

        btnOn.setOnClickListener {
            //viewModel.confirmAccess(userUniqueId)

            publishMessage("On")
        }

        btnOff.setOnClickListener {

            publishMessage("Off")
        }

        btnOff.setOnClickListener {

        }

        mqttConnection()

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

                    Toast.makeText(this@SensorActivity, "MQTT Connection success", Toast.LENGTH_SHORT)
                        .show()

                    subscribeUser()

                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure: ${exception.toString()}")

                    Toast.makeText(
                        this@SensorActivity,
                        "MQTT Connection fails: ${exception.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Receive message: ${message.toString()} from topic: $topic"
                    Log.d(TAG, msg)

                    if (topic == "device") {
                        Toast.makeText(this@SensorActivity, "Temperature : ${message.toString()}", Toast.LENGTH_SHORT).show()
                        textViewTemperature.text = message.toString()
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

        val topic = "device"

        if (mqttClient.isConnected()) {
            mqttClient.subscribe(topic,
                1,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Subscribed to: $topic"
                        Log.d(TAG, msg)

                        Toast.makeText(this@SensorActivity, msg, Toast.LENGTH_SHORT).show()

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Failed to subscribe: $topic")
                    }
                })
        } else {
            Log.d(TAG, "Impossible to subscribe, no server connected")
        }

    }

    private fun publishMessage(message : String) {

        val topic   = "user"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {

                        val msg = "Publish message: $message to topic: $topic"

                        Log.d(this.javaClass.name, msg)
                        Toast.makeText(this@SensorActivity, "Light $message", Toast.LENGTH_SHORT).show()


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