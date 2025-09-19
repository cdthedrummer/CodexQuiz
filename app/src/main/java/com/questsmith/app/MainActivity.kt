package com.questsmith.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.questsmith.app.navigation.NavRoute
import com.questsmith.app.theme.QuestSmithTheme
import com.questsmith.app.ui.WelcomeScreen
import com.questsmith.app.quiz.QuizScreen
import com.questsmith.app.quiz.QuizViewModel
import com.questsmith.app.ui.ResultsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { QuestSmithAppRoot() }
    }
}

@Composable
fun QuestSmithAppRoot() {
    QuestSmithTheme {
        Surface {
            QuestSmithNavHost()
        }
    }
}

@Composable
private fun QuestSmithNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoute.Welcome.route) {
        composable(NavRoute.Welcome.route) {
            WelcomeScreen(onStartQuiz = { navController.navigate(NavRoute.quiz(0)) })
        }
        composable(NavRoute.Quiz.route) { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val viewModel: QuizViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            QuizScreen(
                state = state,
                onOptionToggled = { questionId, optionId ->
                    viewModel.onOptionToggled(questionId, optionId)
                },
                onScaleChanged = { questionId, value ->
                    viewModel.onScaleChanged(questionId, value)
                },
                onNext = {
                    if (index + 1 >= state.questions.size) {
                        viewModel.onSubmit()
                        navController.navigate(NavRoute.Results.route) {
                            popUpTo(NavRoute.Welcome.route) { inclusive = false }
                        }
                    } else {
                        navController.navigate(NavRoute.quiz(index + 1))
                    }
                },
                onBack = {
                    if (index == 0) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(NavRoute.quiz(index - 1)) {
                            popUpTo(NavRoute.quiz(index)) { inclusive = true }
                        }
                    }
                },
                questionIndex = index
            )
        }
        composable(NavRoute.Results.route) {
            val viewModel: QuizViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            ResultsScreen(state = state, onGenerateSummary = viewModel::onGenerateSummary)
        }
    }
}
