package com.questsmith.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String,
    val title: String,
    val kind: QuestionKind,
    val options: List<Option>,
    val statWeights: Map<String, List<Stat>>
) {
    val allowsMultiple: Boolean
        get() = kind == QuestionKind.Multi

    companion object {
        const val SCALE_KEY = "scale"
    }
}

enum class QuestionKind {
    Single,
    Multi,
    Scale
}
