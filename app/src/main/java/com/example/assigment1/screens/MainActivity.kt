package com.example.assigment1.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.assigment1.data.db.BookmarkDatabase
import com.example.assigment1.data.repository.ApiRepository
import com.example.assigment1.data.repository.BookmarkRepositoryImpl
import com.example.assigment1.screens.common.QuizNavGraph
import com.example.assigment1.viewmodel.QuizViewModel
import com.example.assigment1.viewmodel.QuizViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database and repository
        val bookmarkDatabase by lazy {
            Room.databaseBuilder(
                applicationContext,
                BookmarkDatabase::class.java,
                "bookmark_db"
            ).build()
        }

        // Initialize bookmarkRepository
        val bookmarkRepository by lazy {
            BookmarkRepositoryImpl(bookmarkDatabase.bookmarkDao())
        }

        // Initialize apiRepository
        val apiRepository = ApiRepository()

        // Initialize ViewModel using factory
        val viewModel: QuizViewModel by viewModels {
            QuizViewModelFactory(application, bookmarkRepository, apiRepository)
        }

        // Add Navgraph under setContent
        setContent {
            var selectedCountry by rememberSaveable { mutableStateOf("India") }
            val navController = rememberNavController()

            QuizNavGraph(
                navController = navController,
                viewModel = viewModel,
                selectedCountry = selectedCountry,
                onCountryChange = { selectedCountry = it }
            )
        }
    }
}


