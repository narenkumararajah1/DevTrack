package com.naren.devtrack.domain.model

data class Project(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val completionPercentage: Int = 0,
    val isArchived: Boolean = false,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)
