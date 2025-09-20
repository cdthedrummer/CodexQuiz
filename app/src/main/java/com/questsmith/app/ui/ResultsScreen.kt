package com.questsmith.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.questsmith.app.quiz.QuizUiState
import com.questsmith.app.theme.spacing
import com.questsmith.core.domain.model.ResultBlurb
import com.questsmith.core.domain.model.Stat
import com.questsmith.app.R

@Composable
fun ResultsScreen(
    state: QuizUiState,
    onGenerateSummary: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
        ) {
            Text(
                text = "Quest complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        text = "Top stats",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                items(state.topStats) { stat ->
                    StatTile(stat = stat, value = state.tally[stat] ?: 0)
                }
                if (state.resultBlurbs.isNotEmpty()) {
                    item {
                        Text(
                            text = "Your coach notes",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(state.resultBlurbs) { blurb ->
                        ResultBlurbCard(blurb)
                    }
                }
                if (!state.aiSummary.isNullOrBlank()) {
                    item {
                        Text(
                            text = state.aiSummary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                state.errorMessage?.let { message ->
                    item {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Button(
                onClick = onGenerateSummary,
                enabled = !state.isGeneratingSummary
            ) {
                if (state.isGeneratingSummary) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .padding(end = MaterialTheme.spacing.sm)
                            .size(20.dp)
                    )
                    Text(text = stringResource(id = R.string.generating_ai_summary))
                } else {
                    Text(text = stringResource(id = R.string.generate_ai_summary))
                }
            }
        }
    }
}

@Composable
private fun StatTile(stat: Stat, value: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        Text(
            text = "${stat.emoji} ${stat.displayName}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Score: $value",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ResultBlurbCard(blurb: ResultBlurb) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        Text(text = blurb.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = blurb.description, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "Starter actions: ${blurb.starters.joinToString()}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
