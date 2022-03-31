package com.codingstudio.shamirsecretsharing.model

data class User(
    val user_request_id : String ?= null,
    val fk_user_device_id : String ?= null,
    val message : String ?= null,
    val is_active : String ?= null,
    val is_processed : String ?= null,
    val is_key_generated : String ?= null,
    val date_time : String ?= null
)
