package com.questsmith.core.domain.model

data class QuizResponse(
    val selections: Map<String, List<String>>,
    val scaleSelections: Map<String, Int>
)
