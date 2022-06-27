package com.codingstudio.shamirsecretsharing.repository

import com.codingstudio.shamirsecretsharing.api.RetrofitInstance

class Repository() {

    suspend fun getUserInfo(user_name : String, device_id : String, fcm : String, type : String) =
        RetrofitInstance.api.getUserInfo(user_name = user_name, device_id = device_id, fcm = fcm, type = type)

    suspend fun getUserInfo2(user_name : String, password : String, device_id : String, fcm : String, type : String) =
        RetrofitInstance.api.getUserInfo2(user_name = user_name, password = password, device_id = device_id, fcm = fcm, type = type)

    suspend fun checkUserRequest(device_id : String) = RetrofitInstance.api.checkUserRequest(device_id = device_id)

    suspend fun checkUserRequestForAdmin() = RetrofitInstance.api.checkUserRequestForAdmin()

    suspend fun deleteAllData() = RetrofitInstance.api.deleteAllData()

    suspend fun getAllConfirmation() = RetrofitInstance.api.getAllConfirmation()

    suspend fun insertUserRequest(device_id : String) = RetrofitInstance.api.insertUserRequest(device_id = device_id)

    suspend fun userRequestForKey(generated_key_id : String) = RetrofitInstance.api.userRequestForKey(generated_key_id = generated_key_id)

    suspend fun deleteRequest() = RetrofitInstance.api.deleteRequest()

    suspend fun sendUserToDevice(device_id : String) = RetrofitInstance.api.sendUserToDevice(device_id = device_id)

    suspend fun checkUserGeneratedKey(device_id : String) = RetrofitInstance.api.checkUserGeneratedKey(device_id = device_id)

    suspend fun confirmAccess(device_id : String) = RetrofitInstance.api.confirmAccess(device_id = device_id)

    suspend fun adminProcessed(device_id : String, hash_keys : String) = RetrofitInstance.api.adminProcessed(device_id = device_id, hash_keys = hash_keys)

    suspend fun adminSetUserNo(user_no : String, percentage : String) = RetrofitInstance.api.adminSetUserNo(user_no = user_no, percentage = percentage)

    suspend fun checkAdminInfo() = RetrofitInstance.api.checkAdminInfo()

    suspend fun getAllUser() = RetrofitInstance.api.getAllUser()

}