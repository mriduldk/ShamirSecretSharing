package com.codingstudio.shamirsecretsharing.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingstudio.shamirsecretsharing.R
import com.codingstudio.shamirsecretsharing.model.GeneratedKey
import com.codingstudio.shamirsecretsharing.model.Resource
import com.codingstudio.shamirsecretsharing.model.User
import com.codingstudio.shamirsecretsharing.ui.activity.adapter.AdapterUserList
import com.codingstudio.shamirsecretsharing.ui.activity.mqtt.MQTTClient
import com.codingstudio.shamirsecretsharing.ui.viewmodel.SecretSharingViewModel
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.codingstudio.shamirsecretsharing.utils.SharedPref
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.activity_user.imageViewLogout
import kotlinx.android.synthetic.main.activity_user.relativeLayoutParent
import kotlinx.android.synthetic.main.activity_user.relativeLayoutProgressBar
import org.eclipse.paho.client.mqttv3.*


class UserActivity : AppCompatActivity() {

    private val TAG = "UserActivity"
    private lateinit var viewModel: SecretSharingViewModel
    private var userUniqueId: String = ""
    private var generatedKey: List<GeneratedKey> ?= null
    private lateinit var mqttClient : MQTTClient
    private lateinit var adapterUser : AdapterUserList

    private var user1Confirm = false
    private var user2Confirm = false
    private var user3Confirm = false

    private var userListAll = ArrayList<User>()
    private var userList = ArrayList<User>()
    private var key1 = ""
    private var key2 = ""
    private var key3 = ""
    private var user1Name = ""
    private var user2Name = ""
    private var user3Name = ""
    private var userId = ""
    private var deviceName = ""
    private var intensity = 1F

    private var lowerBound = 0
    private var percentage = 0
    private var threshold = 0
    private var noOfUser = 0
    private var noOfConfiremedUser = 0

    private var keyList : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        viewModel = ViewModelProvider(this).get(SecretSharingViewModel::class.java)
        adapterUser = AdapterUserList(this)
        recyclerView.apply {
            adapter = adapterUser
            layoutManager = LinearLayoutManager(this@UserActivity)
        }

        adapterUser.setOnUserClickedListener { user, position ->

            publishMessageForSentRequestToUser(user.device_id ?: "")

        }


        //textViewRequestUser1.background = ContextCompat.getDrawable(this@UserActivity, R.drawable.textview_bg_green)
        //textViewRequestUser2.background = ContextCompat.getDrawable(this@UserActivity, R.drawable.textview_bg_green)
        //textViewRequestUser3.background = ContextCompat.getDrawable(this@UserActivity, R.drawable.textview_bg_green)

        getDeviceId()
        getUserInfo()
        setOnclickListeners()
        observe()
        mqttConnection()

    }

    private fun setOnclickListeners() {

        relativeLayoutBulb.setOnClickListener {

            //viewModel.insertUserRequest(device_id = userUniqueId)
            deviceName = "Bulb"
            publishMessageForUserRequest()
        }

        relativeLayoutAC.setOnClickListener {

            //viewModel.insertUserRequest(device_id = userUniqueId)
            deviceName = "AC"
            publishMessageForUserRequest()
        }

        relativeLayoutCCTV.setOnClickListener {

            //viewModel.insertUserRequest(device_id = userUniqueId)
            deviceName = "CCTV"
            publishMessageForUserRequest()
        }

        imageViewLogout.setOnClickListener {

            SharedPref().logoutUser(this)
            finish()
        }

        textViewSendToDevice.setOnClickListener {

            //viewModel.sendUserToDevice(device_id = userUniqueId)
            publishMessageForDevice()

        }

        textViewTurnOff.setOnClickListener {

            //viewModel.sendUserToDevice(device_id = userUniqueId)
            publishMessageForDeviceTurnOff()

        }

        textViewRefresh.setOnClickListener {

            viewModel.getAllConfirmation()
        }

        /*textViewRequestUser1.setOnClickListener {

            *//*generatedKey?.get(0)?.let { it1 -> it1.generated_key_id?.let { it2 ->
                viewModel.userRequestForKey(
                    it2
                )
            } }*//*

            linearLayoutUserRequest1.visibility = GONE
            linearLayoutUserStatus1.visibility = VISIBLE

            if (userList.size >= 1 ) {
                publishMessageForSentRequestToUser(userList[0].device_id ?: "")
            }
        }

        textViewRequestUser2.setOnClickListener {

            *//*generatedKey?.get(1)?.let { it1 -> it1.generated_key_id?.let { it2 ->
                viewModel.userRequestForKey(
                    it2
                )
            } }*//*

            linearLayoutUserRequest2.visibility = GONE
            linearLayoutUserStatus2.visibility = VISIBLE

            if (userList.size >= 2 ) {
                publishMessageForSentRequestToUser(userList[1].device_id ?: "")
            }
        }

        textViewRequestUser3.setOnClickListener {

            *//*generatedKey?.get(0)?.let { it1 -> it1.generated_key_id?.let { it2 ->
                viewModel.userRequestForKey(
                    it2
                )
            } }*//*

            linearLayoutUserRequest3.visibility = GONE
            linearLayoutUserStatus3.visibility = VISIBLE

            if (userList.size >= 1 ) {
                publishMessageForSentRequestToUser(userList[2].device_id ?: "")
            }
        }*/

        textViewMinusIntensity.setOnClickListener {

            if (intensity > 0F) {
                intensity = (intensity - 0.5).toFloat()
                textViewValueIntensity.text = "$intensity"
                publishMessageForIntensity(intensity)
            }

        }

        textViewAddIntensity.setOnClickListener {

            if (intensity < 1F) {
                intensity = (intensity + 0.5).toFloat()
                textViewValueIntensity.text = "$intensity"
                publishMessageForIntensity(intensity)
            }
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

    private fun setUserInfo() {

        val userStatus = SharedPref().getStringPref(this, Constant.USER_STATUS)

        if (userStatus == Constant.USER_STATUS_1) {

            //textViewRequestText.visibility = VISIBLE
            //textViewRefresh.visibility = VISIBLE
            linearLayoutActions.visibility = GONE
            textViewSendToDevice.visibility = GONE
            linearLayoutUserInfo.visibility = GONE
            linearLayoutAdminResponse.visibility = VISIBLE

        } else if (userStatus == Constant.USER_STATUS_2) {



        }

    }

    private fun getUserInfo() {

        viewModel.checkUserRequest(userUniqueId)

        //viewModel.getAllConfirmation()

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

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                userListAll = userResponseInsertion.data as ArrayList<User>
                                //userList = userResponseInsertion.data as ArrayList<User>
                                noOfUser = userResponseInsertion.data.size - 1

                                for (item in userListAll) {
                                    if (item.device_id == userUniqueId) {
                                        userId = item.user_name.toString()
                                    } else {
                                        userList.add(item)
                                    }
                                }

                                adapterUser.differ.submitList(userList)

                                try {
                                    user1Name = "${userList[0].user_name}"
                                    user2Name = "${userList[1].user_name}"
                                    user3Name = "${userList[2].user_name}"

                                    //textViewUser1Name.text = user1Name
                                    //textViewUser2Name.text = user2Name
                                    //textViewUser3Name.text = user3Name

                                } catch (e: Exception) {

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

        /*viewModel.insertUserRequest.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
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
                                Snackbar.make(
                                    relativeLayoutParent,
                                    "Something went wrong. Please try again",
                                    Snackbar.LENGTH_LONG
                                ).show()
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
                        relativeLayoutProgressBar.visibility = View.VISIBLE
                    }

                }

            }
        })

        viewModel.sendToDevice.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                textViewRequestText.text =
                                    "Keys are successfully sent to Device and verified."
                                textViewRequestText.visibility = VISIBLE
                                linearLayoutActions.visibility = GONE
                                textViewSendToDevice.visibility = GONE
                                textViewRefresh.visibility = GONE
                                textViewUserResponse1.visibility = GONE
                                textViewUserResponse2.visibility = GONE
                                linearLayoutUserInfo.visibility = VISIBLE

                                //publishMessageToMqtt()

                            } else {
                                Snackbar.make(
                                    relativeLayoutParent,
                                    "Something went wrong. Please try again",
                                    Snackbar.LENGTH_LONG
                                ).show()
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
                        relativeLayoutProgressBar.visibility = View.VISIBLE
                    }

                }

            }
        })

        viewModel.getAllConfirmations.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
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
                                        textViewUserResponse1.setTextColor(
                                            ContextCompat.getColor(
                                                this,
                                                R.color.green
                                            )
                                        )
                                        imageViewUserResponse1.setBackgroundColor(
                                            ContextCompat.getColor(
                                                this,
                                                R.color.green
                                            )
                                        )

                                    } else {
                                        textViewUserResponse1.text = "Not Confirmed"
                                        textViewUserResponse1.setTextColor(
                                            ContextCompat.getColor(
                                                this,
                                                R.color.red
                                            )
                                        )
                                        imageViewUserResponse1.setBackgroundColor(
                                            ContextCompat.getColor(
                                                this,
                                                R.color.red
                                            )
                                        )
                                    }

                                    if (generatedKeys[1].is_active == "1") {
                                        textViewUserResponse2.text = "Confirmed"
                                        textViewUserResponse2.setTextColor(
                                            ContextCompat.getColor(
                                                this,
                                                R.color.green
                                            )
                                        )
                                        imageViewUserResponse2.setBackgroundColor(
                                            ContextCompat.getColor(
                                                this,
                                                R.color.green
                                            )
                                        )
                                    } else {
                                        textViewUserResponse2.text = "Not Confirmed"
                                        textViewUserResponse2.setTextColor(
                                            ContextCompat.getColor(
                                                this,
                                                R.color.red
                                            )
                                        )
                                        imageViewUserResponse2.setBackgroundColor(
                                            ContextCompat.getColor(
                                                this,
                                                R.color.red
                                            )
                                        )
                                    }

                                    if (generatedKeys[0].is_active == "1" && generatedKeys[1].is_active == "1") {
                                        textViewSendToDevice.visibility = VISIBLE
                                        textViewRefresh.visibility = GONE
                                        textViewRequestText.text =
                                            "All the users are confirmed the request. You can now send the keys to the Device by clicking Sent to Device button."

                                    } else {
                                        textViewSendToDevice.visibility = GONE
                                    }

                                } catch (ex: Exception) {
                                    Snackbar.make(relativeLayoutParent, "$ex", Snackbar.LENGTH_LONG)
                                        .show()
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

        viewModel.updateKeyReuestForUser.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                Snackbar.make(
                                    relativeLayoutParent,
                                    "Successfully Requested for Keys.",
                                    Snackbar.LENGTH_LONG
                                ).show()

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
        })*/

        viewModel.adminInfo.observe(this, Observer { res ->
            res.getContentIfNotHandled()?.let { response ->

                when (response) {
                    is Resource.Success -> {
                        relativeLayoutProgressBar.visibility = View.GONE
                        response.data?.let { userResponseInsertion ->

                            if (userResponseInsertion.status == 200) {

                                val adminInfo = userResponseInsertion.data
                                lowerBound = adminInfo.no_of_user
                                percentage = adminInfo.percentage


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

    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId() {

        try {
            userUniqueId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        }catch (ex: Exception) {
            Log.e(TAG, "getDeviceId: $ex")
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

                    Toast.makeText(this@UserActivity, "MQTT Connection success", Toast.LENGTH_SHORT)
                        .show()

                    subscribeUser()

                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure: ${exception.toString()}")

                    Toast.makeText(
                        this@UserActivity,
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

                    val keys = message.toString().split(",")

                    if (topic == "user" && keys[0] == "key_generated") {

                        linearLayoutAdminResponse.visibility = GONE
                        linearLayoutUserInfo.visibility = VISIBLE

                        try {
                            key1 = keys[1]
                            key2 = keys[2]
                            key3 = keys[3]

                            for ((i, key) in keys.withIndex()) {

                                if (i >= 1 && i < keys.size - 2) {

                                    keyList.add(key)
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@UserActivity,
                                "Two user not found.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else if (topic == "user" && keys[0] == "temperature") {

                        linearLayoutTemperature.visibility = VISIBLE
                        textViewTemperature.text = keys[1]
                        Toast.makeText(this@UserActivity, "" + keys[1], Toast.LENGTH_LONG).show()

                    } /*else if (topic == "user" && message.toString() == "${userList[0].device_id},request_accepted") {

                        textViewUserResponse1.text = "Confirmed"
                        textViewUserResponse1.setTextColor(
                            ContextCompat.getColor(
                                this@UserActivity,
                                R.color.green
                            )
                        )

                        textViewRequestUser1.visibility = GONE
                        linearLayoutUserRequest1.visibility = GONE
                        linearLayoutUserStatus1.visibility = VISIBLE

                        DrawableCompat.setTint(
                            DrawableCompat.wrap(imageViewUserResponse1.drawable),
                            ContextCompat.getColor(this@UserActivity, R.color.green)
                        )

                        user1Confirm = true

                        if (user1Confirm && user2Confirm || user1Confirm && user3Confirm || user2Confirm && user3Confirm) {
                            textViewSendToDevice.visibility = VISIBLE
                            textViewRequestText.text =
                                "You can now send the keys to the Device by clicking Sent to Device button."
                        }

                    } else if (topic == "user" && message.toString() == "${userList[1].device_id},request_accepted") {

                        textViewUserResponse2.text = "Confirmed"
                        textViewUserResponse2.setTextColor(
                            ContextCompat.getColor(
                                this@UserActivity,
                                R.color.green
                            )
                        )

                        textViewRequestUser2.visibility = GONE
                        linearLayoutUserRequest2.visibility = GONE
                        linearLayoutUserStatus2.visibility = VISIBLE

                        DrawableCompat.setTint(
                            DrawableCompat.wrap(imageViewUserResponse2.drawable),
                            ContextCompat.getColor(this@UserActivity, R.color.green)
                        )

                        user2Confirm = true

                        if (user1Confirm && user2Confirm || user1Confirm && user3Confirm || user2Confirm && user3Confirm) {
                            textViewSendToDevice.visibility = VISIBLE
                            textViewRequestText.text =
                                "You can now send the keys to the Device by clicking Sent to Device button."
                        }
                    } else if (topic == "user" && message.toString() == "${userList[2].device_id},request_accepted") {

                        textViewUserResponse3.text = "Confirmed"
                        textViewUserResponse3.setTextColor(
                            ContextCompat.getColor(
                                this@UserActivity,
                                R.color.green
                            )
                        )

                        textViewRequestUser3.visibility = GONE
                        linearLayoutUserRequest3.visibility = GONE
                        linearLayoutUserStatus3.visibility = VISIBLE

                        DrawableCompat.setTint(
                            DrawableCompat.wrap(imageViewUserResponse3.drawable),
                            ContextCompat.getColor(this@UserActivity, R.color.green)
                        )

                        user3Confirm = true

                        if (user1Confirm && user2Confirm || user1Confirm && user3Confirm || user2Confirm && user3Confirm) {
                            textViewSendToDevice.visibility = VISIBLE
                            textViewRequestText.text =
                                "You can now send the keys to the Device by clicking Sent to Device button."
                        }

                    } else if (topic == "user" && message.toString() == "${userList[0].device_id},request_rejected") {

                        textViewRequestUser1.visibility = VISIBLE
                        linearLayoutUserRequest1.visibility = VISIBLE
                        linearLayoutUserStatus1.visibility = GONE
                        textViewRequestUser1.text = "Rejected | "
                        textViewRequestUser1.background =
                            ContextCompat.getDrawable(this@UserActivity, R.drawable.textview_bg_red)
                        textViewRequestUser1.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_baseline_refresh_24,
                            0
                        );

                    } else if (topic == "user" && message.toString() == "${userList[1].device_id},request_rejected") {

                        textViewRequestUser2.visibility = VISIBLE
                        linearLayoutUserRequest2.visibility = VISIBLE
                        linearLayoutUserStatus2.visibility = GONE
                        textViewRequestUser2.text = "Rejected | "
                        textViewRequestUser2.background =
                            ContextCompat.getDrawable(this@UserActivity, R.drawable.textview_bg_red)
                        textViewRequestUser2.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_baseline_refresh_24,
                            0
                        );

                    } else if (topic == "user" && message.toString() == "${userList[2].device_id},request_rejected") {

                        textViewRequestUser3.visibility = VISIBLE
                        linearLayoutUserRequest3.visibility = VISIBLE
                        linearLayoutUserStatus3.visibility = GONE
                        textViewRequestUser3.text = "Rejected | "
                        textViewRequestUser3.background =
                            ContextCompat.getDrawable(this@UserActivity, R.drawable.textview_bg_red)
                        textViewRequestUser3.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_baseline_refresh_24,
                            0
                        );

                    } */else {

                        try {
                            val userDeviceId = message.toString().split(",")[0]
                            val receivedStatus = message.toString().split(",")[1]
                            var userInfoReceived : User = User()

                            userList.forEach { user ->

                                if (user.device_id == userDeviceId) {
                                    userInfoReceived = user
                                }
                            }

                            val index = userList.indexOf(userInfoReceived);

                            if (receivedStatus == "request_accepted") {
                                userList[index].status = 2
                            } else {
                                userList[index].status = 3
                            }
                            adapterUser.differ.submitList(userList)
                            adapterUser.notifyDataSetChanged()

                            noOfConfiremedUser++

                            if (noOfConfiremedUser >= threshold) {
                                textViewSendToDevice.visibility = VISIBLE
                                textViewRequestText.text = "You can now send the keys to the Device by clicking Sent to Device button."
                            }


                        } catch (e: Exception) {
                            Log.e(TAG, "messageArrived: ",e )
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

    private fun publishMessageForSentRequestToUser(message: String) {

        val topic   = "user"
        //val message = "user_request" //""user,${generatedKey?.get(0)?.hash_key},${generatedKey?.get(1)?.hash_key}"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Publish message: $message to topic: $topic"
                        Log.d(TAG, msg)
                        Toast.makeText(
                            this@UserActivity,
                            "Request sent to user successfully.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Failed to publish message to topic")
                    }
                })

        } else {
            Log.d(TAG, "Impossible to publish, no server connected")
        }

    }

    private fun publishMessageForUserRequest() {

        threshold = lowerBound
        if (noOfUser * percentage >= lowerBound) {
            threshold = noOfUser * percentage
        }

        val topic   = "admin"
        val message = "user_request,$userId,$deviceName"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)
                        Toast.makeText(
                            this@UserActivity,
                            "User Requested Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        linearLayoutActions.visibility = GONE
                        textViewSendToDevice.visibility = GONE
                        linearLayoutUserInfo.visibility = GONE
                        linearLayoutAdminResponse.visibility = VISIBLE

                        SharedPref().setString(
                            this@UserActivity,
                            Constant.USER_STATUS,
                            Constant.USER_STATUS_1
                        )

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to publish message to topic")
                    }
                })

        } else {
            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
        }

    }

    private fun publishMessageForDevice() {

        val topic   = "device"
        var deviceKey = ""
        keyList.forEach {
            deviceKey += "$it,"
        }

        //val message = "user,$key1,$key2,$key3,$deviceName"
        val message = "user,$deviceKey$deviceName"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)
                        Toast.makeText(this@UserActivity, "Successfully sent to device", Toast.LENGTH_SHORT).show()

                        textViewRequestText.text = "Keys are successfully sent to Device and verified."
                        textViewRequestText.visibility = VISIBLE
                        linearLayoutActions.visibility = GONE
                        textViewSendToDevice.visibility = GONE
                        textViewRefresh.visibility = GONE
                        linearLayoutUserInfo.visibility = VISIBLE
                        textViewTurnOff.visibility = VISIBLE
                        /*linearLayoutUser1.visibility = GONE
                        linearLayoutUser2.visibility = GONE
                        linearLayoutUser3.visibility = GONE*/
                        recyclerView.visibility = GONE

                        if (deviceName == "AC") {
                            linearLayoutIntensity.visibility = VISIBLE
                        } else {
                            linearLayoutIntensity.visibility = GONE
                        }

                        textViewTurnOff.text = "Turn Off $deviceName"

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to publish message to topic")
                    }
                })

        } else {
            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
        }

    }

    private fun publishMessageForDeviceTurnOff() {

        val topic   = "device"
        val message = "off,$deviceName"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)
                        Toast.makeText(this@UserActivity, "Successfully sent to device", Toast.LENGTH_SHORT).show()

                        textViewRequestText.text = "$deviceName Turned off successfully."
                        textViewRequestText.visibility = VISIBLE
                        linearLayoutActions.visibility = GONE
                        textViewSendToDevice.visibility = GONE
                        textViewRefresh.visibility = GONE
                        linearLayoutUserInfo.visibility = VISIBLE
                        textViewTurnOff.visibility = GONE
                        /*linearLayoutUser1.visibility = GONE
                        linearLayoutUser2.visibility = GONE
                        linearLayoutUser3.visibility = GONE*/

                        recyclerView.visibility = GONE
                        linearLayoutIntensity.visibility = GONE

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to publish message to topic")
                    }
                })

        } else {
            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
        }

    }

    private fun publishMessageForIntensity(value: Float) {

        val topic   = "device"
        val message = "intensity,$deviceName,$value"

        if (mqttClient.isConnected()) {

            mqttClient.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Publish message: $message to topic: $topic"
                        Log.d(this.javaClass.name, msg)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to publish message to topic")
                    }
                })

        } else {
            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
        }

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

                        //Toast.makeText(this@UserActivity, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(TAG, "Failed to subscribe: $topic")
                    }
                })
        } else {
            Log.d(TAG, "Impossible to subscribe, no server connected")
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (mqttClient.isConnected()) {
            // Disconnect from MQTT Broker
            mqttClient.disconnect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Disconnected")

                    Toast.makeText(
                        this@UserActivity,
                        "MQTT Disconnection success",
                        Toast.LENGTH_SHORT
                    ).show()

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