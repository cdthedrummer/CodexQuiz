package com.questsmith.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizResult(
    val responses: Map<String, List<String>>,
    val scaleResponses: Map<String, Int>,
    val tally: Map<Stat, Int>,
    val aiSummary: String? = null
)
