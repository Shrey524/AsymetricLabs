package com.example.assigment1.screens.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.assigment1.viewmodel.QuizViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun BookmarkedQuestionsScreen(
    viewModel: QuizViewModel = viewModel(),
    onCloseClick: () -> Unit,
    navController: NavController
) {
    val bookmarkedQuestions by viewModel.bookmarkedQuestions.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Close button or Back button
        IconButton(onClick = { onCloseClick() }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }

        // If no bookmarked questions are present
        if (bookmarkedQuestions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No bookmarked questions yet.")
            }
        } else {
            // If there are bookmarked questions, navigate to the QuestionScreen
            LaunchedEffect(Unit) {
                navController.navigate("questions?isBookmarkMode=true")
            }
        }
    }
}