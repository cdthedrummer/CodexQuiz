package com.questsmith.core.domain.usecase

import com.questsmith.core.domain.model.Question
import com.questsmith.core.domain.model.QuestionKind
import com.questsmith.core.domain.model.QuizResponse
import com.questsmith.core.domain.model.Stat
import javax.inject.Inject

class ScoreQuizUseCase @Inject constructor() {
    operator fun invoke(questions: List<Question>, response: QuizResponse): Map<Stat, Int> {
        val tally = mutableMapOf<Stat, Int>().withDefault { 0 }
        val selections = response.selections
        val scaleSelections = response.scaleSelections

        questions.forEach { question ->
            when (question.kind) {
                QuestionKind.Single, QuestionKind.Multi -> {
                    val answers = selections[question.id].orEmpty()
                    answers.forEach { optionId ->
                        val stats = question.statWeights[optionId].orEmpty()
                        stats.forEach { stat ->
                            val current = tally.getValue(stat)
                            tally[stat] = current + 1
                        }
                    }
                }
                QuestionKind.Scale -> {
                    val value = scaleSelections[question.id] ?: return@forEach
                    val stats = question.statWeights[Question.SCALE_KEY].orEmpty()
                    stats.forEach { stat ->
                        val current = tally.getValue(stat)
                        tally[stat] = current + value
                    }
                }
            }
        }

        return Stat.entries.associateWith { tally.getValue(it) }
    }
}
