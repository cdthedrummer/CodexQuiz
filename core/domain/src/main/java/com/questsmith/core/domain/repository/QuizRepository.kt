package com.questsmith.core.domain.repository

import com.questsmith.core.domain.model.Question
import com.questsmith.core.domain.model.QuizResult
import com.questsmith.core.domain.model.Stat

interface QuizRepository {
    fun seed(): List<Question>
    suspend fun saveResult(tally: Map<Stat, Int>, aiSummary: String? = null)
    suspend fun generateCoachSummary(result: QuizResult): String
}
