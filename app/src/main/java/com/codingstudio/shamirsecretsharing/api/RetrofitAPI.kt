package com.codingstudio.shamirsecretsharing.api

import com.codingstudio.shamirsecretsharing.model.ResponseAdminInfo
import com.codingstudio.shamirsecretsharing.model.ResponseInsertion
import com.codingstudio.shamirsecretsharing.model.ResponseKeyGenerated
import com.codingstudio.shamirsecretsharing.model.ResponseUser
import retrofit2.Response
import retrofit2.http.*

interface RetrofitAPI {


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun getUserInfo(
        @Field("view")
        view: String = "userInfo",
        @Field("user_name")
        user_name: String,
        @Field("device_id")
        device_id: String,
        @Field("fcm")
        fcm: String,
        @Field("type")
        type: String
    ): Response<ResponseInsertion>

    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun getUserInfo2(
        @Field("view")
        view: String = "userInfo2",
        @Field("user_name")
        user_name: String,
        @Field("password")
        password: String,
        @Field("device_id")
        device_id: String,
        @Field("fcm")
        fcm: String,
        @Field("type")
        type: String
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun checkUserRequestForAdmin(
        @Field("view")
        view: String = "checkUserRequestForAdmin"
    ): Response<ResponseUser>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun getAllConfirmation(
        @Field("view")
        view: String = "getAllConfirmation"
    ): Response<ResponseKeyGenerated>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun checkUserRequest(
        @Field("view")
        view: String = "checkUserRequest",
        @Field("device_id")
        device_id: String
    ): Response<ResponseUser>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun insertUserRequest(
        @Field("view")
        view: String = "insertUserRequest",
        @Field("device_id")
        device_id: String
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun userRequestForKey(
        @Field("view")
        view: String = "userRequestForKey",
        @Field("generated_key_id")
        generated_key_id: String
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun deleteRequest(
        @Field("view")
        view: String = "deleteRequest"
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun deleteAllData(
        @Field("view")
        view: String = "deleteAllData"
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun sendUserToDevice(
        @Field("view")
        view: String = "sendUserToDevice",
        @Field("device_id")
        device_id: String
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun checkUserGeneratedKey(
        @Field("view")
        view: String = "checkUserGeneratedKey",
        @Field("device_id")
        device_id: String
    ): Response<ResponseKeyGenerated>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun confirmAccess(
        @Field("view")
        view: String = "confirmAccess",
        @Field("device_id")
        device_id: String
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun adminProcessed(
        @Field("view")
        view: String = "adminProcessed",
        @Field("device_id")
        device_id: String,
        @Field("hash_keys")
        hash_keys: String
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun adminSetUserNo(
        @Field("view")
        view: String = "adminSetUserNo",
        @Field("user_no")
        user_no: String,
        @Field("percentage")
        percentage: String
    ): Response<ResponseInsertion>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun checkAdminInfo(
        @Field("view")
        view: String = "checkAdminInfo"
    ): Response<ResponseAdminInfo>


    @POST("user/UserController.php")
    @FormUrlEncoded
    suspend fun getAllUser(
        @Field("view")
        view: String = "getAllUser"
    ): Response<ResponseUser>











}