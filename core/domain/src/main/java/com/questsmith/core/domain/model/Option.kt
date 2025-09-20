package com.questsmith.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Option(
    val id: String,
    val label: String,
    val description: String? = null
)
