package com.questsmith.core.domain.usecase

import com.questsmith.core.domain.model.Stat
import com.questsmith.core.domain.repository.QuizRepository
import javax.inject.Inject

class SaveResultUseCase @Inject constructor(private val repository: QuizRepository) {
    suspend operator fun invoke(tally: Map<Stat, Int>, aiSummary: String?) {
        repository.saveResult(tally, aiSummary)
    }
}
