package com.questsmith.app.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.questsmith.app.theme.spacing
import com.questsmith.core.domain.model.Option
import com.questsmith.core.domain.model.Question
import com.questsmith.core.domain.model.QuestionKind

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    state: QuizUiState,
    questionIndex: Int,
    onOptionToggled: (String, String) -> Unit,
    onScaleChanged: (String, Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val questions = state.questions
    if (questions.isEmpty()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "Loading questions...")
            }
        }
        return
    }
    val question = questions.getOrNull(questionIndex) ?: questions.first()
    val total = questions.size
    val currentSelections = state.selections[question.id].orEmpty()
    val currentScale = state.scaleSelections[question.id]
    val isValid = when (question.kind) {
        QuestionKind.Scale -> currentScale != null
        QuestionKind.Multi -> currentSelections.isNotEmpty()
        QuestionKind.Single -> currentSelections.isNotEmpty()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                Text(
                    text = "Question ${questionIndex + 1} of $total",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                AnimatedContent(
                    targetState = question,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { it / 2 },
                            animationSpec = tween(180)
                        ) togetherWith fadeOut(animationSpec = tween(150))
                    },
                    label = "question"
                ) { current ->
                    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                        Text(
                            text = current.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        when (current.kind) {
                            QuestionKind.Scale -> ScaleSelector(
                                selected = currentScale ?: 0,
                                onSelected = { value -> onScaleChanged(current.id, value) }
                            )
                            QuestionKind.Single -> OptionList(
                                options = current.options,
                                selected = currentSelections,
                                onOptionToggled = { optionId -> onOptionToggled(current.id, optionId) }
                            )
                            QuestionKind.Multi -> OptionList(
                                options = current.options,
                                selected = currentSelections,
                                onOptionToggled = { optionId -> onOptionToggled(current.id, optionId) }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onBack, enabled = questionIndex > 0) {
                    Text(text = "Back")
                }
                Button(onClick = onNext, enabled = isValid) {
                    Text(text = if (questionIndex == total - 1) "Finish" else "Next")
                }
            }
        }
    }
}

@Composable
private fun OptionList(
    options: List<Option>,
    selected: List<String>,
    onOptionToggled: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(options) { option ->
            OptionChip(
                option = option,
                selected = selected.contains(option.id),
                onClick = { onOptionToggled(option.id) }
            )
        }
    }
}

@Composable
fun OptionChip(
    option: Option,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(option.label, style = MaterialTheme.typography.bodyLarge) },
        modifier = Modifier.fillMaxWidth(),
        enabled = true
    )
}

@Composable
fun ScaleSelector(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        (1..3).forEach { value ->
            Button(
                onClick = { onSelected(value) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected == value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (selected == value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(text = value.toString())
            }
        }
    }
}
