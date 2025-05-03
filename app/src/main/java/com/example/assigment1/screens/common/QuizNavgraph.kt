package com.example.assigment1.screens.common

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.assigment1.screens.bookmark.BookmarkedQuestionsScreen
import com.example.assigment1.screens.home.HomeScreen
import com.example.assigment1.screens.home.SplashScreen
import com.example.assigment1.screens.quiz.QuestionScreen
import com.example.assigment1.screens.resultscreen.ResultScreen
import com.example.assigment1.viewmodel.QuizViewModel

@Composable
fun QuizNavGraph(
    navController: NavHostController,
    viewModel: QuizViewModel,
    selectedCountry: String,
    onCountryChange: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = "splash") {

        // Splash Screen
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // Home Screen
        composable("home") {
            HomeScreen(
                selectedCountry = selectedCountry,
                countryList = listOf("India", "USA", "Germany"),
                onCountrySelected = onCountryChange,
                onStartClicked = { timer ->
                    viewModel.updateTimerDuration(timer)
                    navController.navigate("questions?isBookmarkMode=false")
                },
                onBookmarksClicked = { timer ->
                    viewModel.updateTimerDuration(timer)
                    navController.navigate("bookmarks")
                }
            )
        }

        // Bookmarked Questions Screen
        composable("bookmarks") {
            viewModel.resetQuiz()
            BookmarkedQuestionsScreen(
                viewModel = viewModel,
                onCloseClick = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

        // Questions Screen
        composable(
            "questions?isBookmarkMode={isBookmarkMode}",
            arguments = listOf(navArgument("isBookmarkMode") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isBookmarkMode = backStackEntry.arguments?.getBoolean("isBookmarkMode") ?: false

            viewModel.resetQuiz()
            QuestionScreen(
                viewModel = viewModel,
                navController = navController,
                isBookmarkMode = isBookmarkMode,
                onCloseClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onQuizFinished = { correctAnswers, totalQuestions ->
                    navController.navigate("result/$correctAnswers/$totalQuestions")
                }
            )
        }

        // Result Screen
        composable(
            "result/{correctAnswers}/{totalQuestions}",
            arguments = listOf(
                navArgument("correctAnswers") { type = NavType.IntType },
                navArgument("totalQuestions") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val correctAnswers = backStackEntry.arguments?.getInt("correctAnswers") ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 1

            ResultScreen(
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions,
                onContinueClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

