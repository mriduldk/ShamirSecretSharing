package com.codingstudio.shamirsecretsharing.model

data class ResponseUser(
    val status: Int,
    val message: String,
    val data: List<User>
)