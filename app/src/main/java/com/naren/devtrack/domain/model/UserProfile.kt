package com.naren.devtrack.domain.model

data class UserProfile(
    val uid: String,
    val email: String?,
    val createdAt: Long? = null
)
