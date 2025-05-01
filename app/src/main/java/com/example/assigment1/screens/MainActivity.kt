package com.example.assigment1.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.example.assigment1.screens.home.HomeScreen
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.assigment1.R
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.assigment1.data.db.BookmarkDatabase
import com.example.assigment1.data.repository.BookmarkRepositoryImpl
import com.example.assigment1.screens.bookmark.BookmarkedQuestionsScreen
import com.example.assigment1.screens.home.SplashScreen
import com.example.assigment1.screens.quiz.QuestionScreen
import com.example.assigment1.screens.resultscreen.ResultScreen
import com.example.assigment1.viewmodel.QuizViewModel
import com.example.assigment1.viewmodel.QuizViewModelFactory
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository
        val bookmarkDatabase by lazy {
            Room.databaseBuilder(
                applicationContext,
                BookmarkDatabase::class.java,
                "bookmark_db"
            ).build()
        }

        val bookmarkRepository by lazy {
            BookmarkRepositoryImpl(bookmarkDatabase.bookmarkDao())
        }

        // Initialize ViewModel
        val viewModel: QuizViewModel by viewModels {
            QuizViewModelFactory(application, bookmarkRepository)
        }

        setContent {
            val navController = rememberNavController()
            var selectedCountry by rememberSaveable { mutableStateOf("India") }

            NavHost(navController = navController, startDestination = "splash") {
                composable("splash") {
                    SplashScreen(navController = navController)
                }

                composable("home") {
                    HomeScreen(
                        selectedCountry = selectedCountry,
                        countryList = listOf("India", "USA", "Germany"),
                        onCountrySelected = { selectedCountry = it },
                        onStartClicked = {
                            navController.navigate("questions?isBookmarkMode=false")
                        },
                        onBookmarksClicked = {
                            navController.navigate("bookmarks")
                        }
                    )
                }

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

                composable("questions?isBookmarkMode={isBookmarkMode}",
                    arguments = listOf(navArgument("isBookmarkMode") { type = NavType.BoolType })) { backStackEntry ->

                    // Retrieve the isBookmarkMode argument
                    val isBookmarkMode = backStackEntry.arguments?.getBoolean("isBookmarkMode") ?: false

                    viewModel.resetQuiz()
                    QuestionScreen(
                        viewModel = viewModel,
                        navController = navController,
                        isBookmarkMode = isBookmarkMode, // Pass the value here
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
    }
}
