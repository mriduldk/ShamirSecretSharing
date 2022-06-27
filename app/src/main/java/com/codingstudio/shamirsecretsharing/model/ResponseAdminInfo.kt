package com.codingstudio.shamirsecretsharing.model

data class ResponseAdminInfo(
    val status: Int,
    val message: String,
    val data: AdminInfo
)