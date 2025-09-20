package com.questsmith.app.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.questsmith.core.domain.model.Question
import com.questsmith.core.domain.model.QuestionKind
import com.questsmith.core.domain.model.QuizResponse
import com.questsmith.core.domain.model.QuizResult
import com.questsmith.core.domain.model.ResultBlurb
import com.questsmith.core.domain.model.ResultDescriptor
import com.questsmith.core.domain.model.Stat
import com.questsmith.core.domain.repository.QuizRepository
import com.questsmith.core.domain.usecase.SaveResultUseCase
import com.questsmith.core.domain.usecase.ScoreQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val scoreQuiz: ScoreQuizUseCase,
    private val saveResult: SaveResultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState(questions = quizRepository.seed()))
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onOptionToggled(questionId: String, optionId: String) {
        val state = _uiState.value
        val question = state.questions.find { it.id == questionId } ?: return
        val current = state.selections[questionId].orEmpty().toMutableList()
        if (question.kind == QuestionKind.Single) {
            current.clear()
            current.add(optionId)
        } else {
            if (current.contains(optionId)) current.remove(optionId) else current.add(optionId)
        }
        _uiState.value = state.copy(selections = state.selections.toMutableMap().apply {
            put(questionId, current)
        })
    }

    fun onScaleChanged(questionId: String, value: Int) {
        val state = _uiState.value
        _uiState.value = state.copy(
            scaleSelections = state.scaleSelections.toMutableMap().apply { put(questionId, value) }
        )
    }

    fun onSubmit() {
        val state = _uiState.value
        val response = QuizResponse(state.selections, state.scaleSelections)
        val tally = scoreQuiz(state.questions, response)
        val topStats = tally.entries.sortedByDescending { it.value }.map { it.key }.take(3)
        val descriptors = ResultDescriptor.describe(topStats)
        val result = QuizResult(
            responses = state.selections,
            scaleResponses = state.scaleSelections,
            tally = tally
        )
        _uiState.value = state.copy(
            tally = tally,
            topStats = topStats,
            resultBlurbs = descriptors,
            quizResult = result
        )
        viewModelScope.launch {
            saveResult(tally, state.aiSummary)
        }
    }

    fun onGenerateSummary() {
        val state = _uiState.value
        val quizResult = state.quizResult ?: return
        viewModelScope.launch {
            _uiState.value = state.copy(isGeneratingSummary = true, errorMessage = null)
            runCatching {
                quizRepository.generateCoachSummary(quizResult)
            }.onSuccess { summary ->
                val updatedResult = quizResult.copy(aiSummary = summary)
                _uiState.value = _uiState.value.copy(
                    isGeneratingSummary = false,
                    aiSummary = summary,
                    quizResult = updatedResult
                )
                saveResult(_uiState.value.tally, summary)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingSummary = false,
                    errorMessage = throwable.message
                )
            }
        }
    }
}

data class QuizUiState(
    val questions: List<Question>,
    val selections: Map<String, List<String>> = emptyMap(),
    val scaleSelections: Map<String, Int> = emptyMap(),
    val tally: Map<Stat, Int> = emptyMap(),
    val topStats: List<Stat> = emptyList(),
    val resultBlurbs: List<ResultBlurb> = emptyList(),
    val quizResult: QuizResult? = null,
    val aiSummary: String? = null,
    val isGeneratingSummary: Boolean = false,
    val errorMessage: String? = null
) {
    fun currentSelectionCount(question: Question): Int = when (question.kind) {
        QuestionKind.Scale -> if (scaleSelections[question.id] != null) 1 else 0
        else -> selections[question.id].orEmpty().size
    }
}
