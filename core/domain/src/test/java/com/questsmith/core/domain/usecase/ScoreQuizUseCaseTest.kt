package com.questsmith.core.domain.usecase

import com.questsmith.core.domain.model.Option
import com.questsmith.core.domain.model.Question
import com.questsmith.core.domain.model.QuestionKind
import com.questsmith.core.domain.model.QuizResponse
import com.questsmith.core.domain.model.Stat
import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreQuizUseCaseTest {
    private val useCase = ScoreQuizUseCase()

    @Test
    fun `single choice answers increment tally`() {
        val questions = listOf(
            Question(
                id = "q1",
                title = "Test",
                kind = QuestionKind.Single,
                options = listOf(Option("o1", "One")),
                statWeights = mapOf("o1" to listOf(Stat.STRENGTH))
            )
        )
        val response = QuizResponse(
            selections = mapOf("q1" to listOf("o1")),
            scaleSelections = emptyMap()
        )

        val tally = useCase(questions, response)

        assertEquals(1, tally[Stat.STRENGTH])
    }

    @Test
    fun `scale questions add weighted values`() {
        val questions = listOf(
            Question(
                id = "q1",
                title = "Scale",
                kind = QuestionKind.Scale,
                options = emptyList(),
                statWeights = mapOf(Question.SCALE_KEY to listOf(Stat.WISDOM))
            )
        )
        val response = QuizResponse(
            selections = emptyMap(),
            scaleSelections = mapOf("q1" to 3)
        )

        val tally = useCase(questions, response)

        assertEquals(3, tally[Stat.WISDOM])
    }
}
