package com.codingstudio.shamirsecretsharing.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.codingstudio.shamirsecretsharing.model.*
import com.codingstudio.shamirsecretsharing.repository.Repository
import com.codingstudio.shamirsecretsharing.ui.activity.AppApplication
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.codingstudio.shamirsecretsharing.utils.EventWrapper
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SecretSharingViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "SecretSharingViewModel"
    private var repository: Repository = Repository()

    val userInfo: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val userInfo2: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val userRequest: MutableLiveData<EventWrapper<Resource<ResponseUser>>> = MutableLiveData()
    val userRequestAdmin: MutableLiveData<EventWrapper<Resource<ResponseUser>>> = MutableLiveData()
    val deleteAllData: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val adminInfo: MutableLiveData<EventWrapper<Resource<ResponseAdminInfo>>> = MutableLiveData()
    val getAllUser: MutableLiveData<EventWrapper<Resource<ResponseUser>>> = MutableLiveData()
    val getAllConfirmations: MutableLiveData<EventWrapper<Resource<ResponseKeyGenerated>>> = MutableLiveData()
    val insertUserRequest: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val updateKeyReuestForUser: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val deleteRequest: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val sendToDevice: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val checkUserGeneratedKey: MutableLiveData<EventWrapper<Resource<ResponseKeyGenerated>>> = MutableLiveData()
    val confirmAccess: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val adminProcessed: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()
    val adminUserNo: MutableLiveData<EventWrapper<Resource<ResponseInsertion>>> = MutableLiveData()



    fun getUserInfo(user_name : String, device_id : String, fcm : String, type : String) = viewModelScope.launch {

        userInfo.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.getUserInfo(user_name = user_name, device_id = device_id, fcm = fcm, type = type)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        userInfo.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        userInfo.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        userInfo.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        userInfo.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        userInfo.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                userInfo.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> userInfo.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> userInfo.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun getUserInfo2(user_name : String, password : String, device_id : String, fcm : String, type : String) = viewModelScope.launch {

        userInfo2.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.getUserInfo2(user_name = user_name, password = password, device_id = device_id, fcm = fcm, type = type)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        userInfo2.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        userInfo2.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        userInfo2.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        userInfo2.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        userInfo2.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                userInfo2.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> userInfo2.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> userInfo2.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun checkUserRequest(device_id : String) = viewModelScope.launch {

        userRequest.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.checkUserRequest(device_id = device_id)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        userRequest.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        userRequest.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        userRequest.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        userRequest.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        userRequest.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                userRequest.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> userRequest.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> userRequest.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun checkUserRequestForAdmin() = viewModelScope.launch {

        userRequestAdmin.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.checkUserRequestForAdmin()

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        userRequestAdmin.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        userRequestAdmin.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        userRequestAdmin.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        userRequestAdmin.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        userRequestAdmin.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                userRequestAdmin.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> userRequestAdmin.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> userRequestAdmin.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun deleteAllData() = viewModelScope.launch {

        deleteAllData.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.deleteAllData()

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        deleteAllData.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        deleteAllData.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        deleteAllData.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        deleteAllData.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        deleteAllData.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                deleteAllData.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> deleteAllData.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> deleteAllData.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun checkAdminInfo() = viewModelScope.launch {

        adminInfo.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.checkAdminInfo()

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        adminInfo.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        adminInfo.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        adminInfo.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        adminInfo.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        adminInfo.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                adminInfo.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> adminInfo.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> adminInfo.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun getAllUser() = viewModelScope.launch {

        getAllUser.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.getAllUser()

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        getAllUser.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        getAllUser.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        getAllUser.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        getAllUser.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        getAllUser.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                getAllUser.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> getAllUser.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> getAllUser.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun getAllConfirmation() = viewModelScope.launch {

        getAllConfirmations.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.getAllConfirmation()

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        getAllConfirmations.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        getAllConfirmations.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        getAllConfirmations.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        getAllConfirmations.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        getAllConfirmations.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                getAllConfirmations.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> getAllConfirmations.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> getAllConfirmations.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun insertUserRequest(device_id : String) = viewModelScope.launch {

        insertUserRequest.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.insertUserRequest(device_id = device_id)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        insertUserRequest.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        insertUserRequest.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        insertUserRequest.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        insertUserRequest.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        insertUserRequest.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                insertUserRequest.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> insertUserRequest.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> insertUserRequest.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun userRequestForKey(generated_key_id : String) = viewModelScope.launch {

        updateKeyReuestForUser.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.userRequestForKey(generated_key_id = generated_key_id)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        updateKeyReuestForUser.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        updateKeyReuestForUser.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        updateKeyReuestForUser.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        updateKeyReuestForUser.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        updateKeyReuestForUser.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                updateKeyReuestForUser.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> updateKeyReuestForUser.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> updateKeyReuestForUser.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun deleteRequest() = viewModelScope.launch {

        deleteRequest.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.deleteRequest()

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        deleteRequest.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        deleteRequest.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        deleteRequest.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        deleteRequest.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        deleteRequest.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                deleteRequest.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> deleteRequest.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> deleteRequest.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun sendUserToDevice(device_id : String) = viewModelScope.launch {

        sendToDevice.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.sendUserToDevice(device_id = device_id)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        sendToDevice.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        sendToDevice.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        sendToDevice.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        sendToDevice.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        sendToDevice.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                sendToDevice.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> sendToDevice.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> sendToDevice.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun checkUserGeneratedKey(device_id : String) = viewModelScope.launch {

        checkUserGeneratedKey.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.checkUserGeneratedKey(device_id = device_id)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        checkUserGeneratedKey.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        checkUserGeneratedKey.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        checkUserGeneratedKey.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        checkUserGeneratedKey.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        checkUserGeneratedKey.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                checkUserGeneratedKey.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> checkUserGeneratedKey.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> checkUserGeneratedKey.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun confirmAccess(device_id : String) = viewModelScope.launch {

        confirmAccess.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.confirmAccess(device_id = device_id)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        confirmAccess.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        confirmAccess.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        confirmAccess.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        confirmAccess.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        confirmAccess.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                confirmAccess.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> confirmAccess.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> confirmAccess.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun adminProcessed(device_id : String, hash_keys : String) = viewModelScope.launch {

        adminProcessed.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.adminProcessed(device_id = device_id, hash_keys = hash_keys)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        adminProcessed.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        adminProcessed.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        adminProcessed.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        adminProcessed.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        adminProcessed.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                adminProcessed.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> adminProcessed.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> adminProcessed.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }

    fun adminSetUserNo(user_no : String, percentage : String) = viewModelScope.launch {

        adminUserNo.postValue(EventWrapper(Resource.Loading()))
        try {
            if (hasInternetConnection()) {
                val response = repository.adminSetUserNo(user_no = user_no, percentage = percentage)

                when {
                    response.code() == Constant.STATUS_SUCCESS -> {

                        adminUserNo.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                    response.code() == Constant.STATUS_NOT_FOUND -> {

                        adminUserNo.postValue(EventWrapper(Resource.Error(Constant.NOT_FOUND)))
                    }
                    response.code() == Constant.STATUS_INTERNAL_ERROR -> {

                        adminUserNo.postValue(EventWrapper(Resource.Error(Constant.SERVER_ERROR)))
                    }
                    response.code() == Constant.STATUS_CONFLICT -> {

                        adminUserNo.postValue(EventWrapper(Resource.Error(Constant.CONFLICT)))
                    }
                    else -> {
                        adminUserNo.postValue(EventWrapper(handleNetworkResponse(response)))
                    }
                }
            } else {
                adminUserNo.postValue(EventWrapper(Resource.Error(Constant.NO_INTERNET)))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> adminUserNo.postValue(EventWrapper(Resource.Error(Constant.NETWORK_FAILURE)))
                else -> adminUserNo.postValue(EventWrapper(Resource.Error(Constant.CONVERSION_ERROR)))
            }
        }
    }





    /// ----------------------- HANDLE NETWORK RESPONSE ------------------------------ ///

    private fun <T> handleNetworkResponse(response: Response<T>): Resource<T> {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    /// ----------------------- CHECK CONNECTION ------------------------------ ///

    private fun hasInternetConnection(): Boolean {

        val connectivityManager = getApplication<AppApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


}