package com.codingstudio.shamirsecretsharing.model

data class GeneratedKey (
    val generated_key_id : String ?= null,
    val fk_user_id : String ?= null,
    val hash_key : String ?= null,
    val is_active : String ?= null,
    val is_requested : String ?= null,
    val date_time : String ?= null
)