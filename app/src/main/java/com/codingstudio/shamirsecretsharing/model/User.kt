package com.codingstudio.shamirsecretsharing.model

data class User(
    val user_request_id : String ?= null,
    val device_id : String ?= null,
    val password : String ?= null,
    val user_name : String ?= null,
    val user_id : String ?= null,
    val is_processed : String ?= null,
    val is_key_generated : String ?= null,
    val type : String ?= null,
    var status : Int = 0
)
