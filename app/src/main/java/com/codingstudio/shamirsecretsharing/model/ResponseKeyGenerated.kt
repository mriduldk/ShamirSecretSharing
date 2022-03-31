package com.codingstudio.shamirsecretsharing.model

data class ResponseKeyGenerated(
    val status: Int,
    val message: String,
    val data: List<GeneratedKey>
)