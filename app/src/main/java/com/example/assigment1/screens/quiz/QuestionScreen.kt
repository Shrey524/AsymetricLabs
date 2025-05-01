package com.example.assigment1.screens.quiz

import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.assigment1.R
import com.example.assigment1.data.model.QuestionResponse
import com.example.assigment1.data.model.SolutionItem
import com.example.assigment1.data.model.BookmarkedQuestionEntity
import com.example.assigment1.viewmodel.QuizViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

@Composable
fun QuestionScreen(
    isBookmarkMode: Boolean = false,
    navController: NavHostController,
    viewModel: QuizViewModel = viewModel(),
    onCloseClick: () -> Unit,
    onQuizFinished: (correctAnswers: Int, totalQuestions: Int) -> Unit
) {
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOptions.collectAsState()
    val timeRemainingPercent by viewModel.timePercent.collectAsState()
    val quizFinished by viewModel.quizFinished.collectAsState()
    val isAnswerSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val isMuted by viewModel.isMuted
    val bookmarked = viewModel.bookmarkedQuestions.collectAsState().value
    val apiQuestions = viewModel.questions.collectAsState().value
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    val questions = rememberUpdatedState(
        if (isBookmarkMode) bookmarked.map { it.toQuestionResponse() } else apiQuestions
    ).value

    val currentQuestion = questions.getOrNull(currentIndex)
    val isBookmarked = currentQuestion?.uuidIdentifier?.let { bookmarkedIds.contains(it) } ?: false

    LaunchedEffect(isMuted) {
        if (isMuted) {
            viewModel.pauseMusic()
        } else {
            viewModel.startMusic()
        }
    }

    LaunchedEffect(quizFinished) {
        if (quizFinished) {
            viewModel.stopAndReleaseMusic()
            onQuizFinished(viewModel.correctAnswers.value, questions.size)
        }
    }

    if (currentQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading question...", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top progress + mute + close
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LinearProgressIndicator(
                progress = { timeRemainingPercent },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(8.dp),
                trackColor = Color.LightGray,
                color = Color.DarkGray
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.toggleMute() }) {
                    Icon(
                        painter = painterResource(id = if (viewModel.isMuted.value) R.drawable.ic_mute else R.drawable.ic_unmute),
                        contentDescription = if (viewModel.isMuted.value) "Mute" else "Unmute",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = {
                    viewModel.stopAndReleaseMusic()
                    if(isBookmarkMode) onCloseClick() else  navController.navigate("home")

                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Question ${currentIndex + 1}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when (currentQuestion.questionType) {
            "text" -> Text(currentQuestion.question, style = MaterialTheme.typography.bodyMedium)
            "htmlText" -> AndroidView(
                factory = { context -> TextView(context).apply { textSize = 18f } },
                update = {
                    it.text = HtmlCompat.fromHtml(currentQuestion.question, HtmlCompat.FROM_HTML_MODE_COMPACT)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            "image" -> AsyncImage(
                model = currentQuestion.question,
                contentDescription = "Question Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            else -> Text("Unsupported question type.", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        val options = listOf(
            currentQuestion.option1,
            currentQuestion.option2,
            currentQuestion.option3,
            currentQuestion.option4
        )

        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isAnswerSubmitted) { viewModel.selectOption(index) }
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = selectedOption == index,
                    onClick = if (!isAnswerSubmitted) { { viewModel.selectOption(index) } } else null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Black,
                        unselectedColor = Color.DarkGray
                    )
                )
                Text(option, modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isAnswerSubmitted) {
            Spacer(modifier = Modifier.height(24.dp))

            val isCorrect = selectedOption == (currentQuestion.correctOption - 1)
            Text(
                if (isCorrect) "Correct Answer!" else "Wrong Answer!",
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text("Explanation:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))

            currentQuestion.solution.forEach { solutionItem ->
                when (solutionItem.contentType) {
                    "text" -> Text(solutionItem.contentData, style = MaterialTheme.typography.bodyMedium)
                    "htmlText" -> AndroidView(
                        factory = { context -> TextView(context).apply { textSize = 16f } },
                        update = {
                            it.text = HtmlCompat.fromHtml(solutionItem.contentData, HtmlCompat.FROM_HTML_MODE_COMPACT)
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    "image" -> AsyncImage(
                        model = solutionItem.contentData,
                        contentDescription = "Explanation Image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    else -> Text("Unsupported content type.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Bookmark + Next/Skip + Submit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = if (isBookmarked) R.drawable.ic_bookmarked else R.drawable.ic_bookmark),
                contentDescription = if (isBookmarked) "Bookmarked" else "Bookmark",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(enabled = !isAnswerSubmitted) {
                        currentQuestion.uuidIdentifier?.let { id ->
                            if (isBookmarked) {
                                viewModel.deleteBookmark(id)
                            } else {
                                viewModel.bookmarkCurrent()
                            }
                        }
                    }
                )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    if (isAnswerSubmitted) viewModel.nextQuestion(isBookmarkMode)
                    else viewModel.skip(isBookmarkMode)
                }) {
                    Text(if (isAnswerSubmitted) "Next" else "Skip", color = Color.Black)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.submit() },
                    enabled = selectedOption != null && !isAnswerSubmitted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Submit")
                }
            }
        }
    }
}


fun BookmarkedQuestionEntity.toQuestionResponse(): QuestionResponse {
    val solutionListType = object : TypeToken<List<SolutionItem>>() {}.type
    val solution = Gson().fromJson<List<SolutionItem>>(this.solutionJson, solutionListType)
    return QuestionResponse(
        uuidIdentifier = this.questionId,
        question = this.questionText,
        questionType = this.questionType,
        option1 = this.option1,
        option2 = this.option2,
        option3 = this.option3,
        option4 = this.option4,
        correctOption = this.correctOption,
        sort = 0,
        solution = solution
    )
}
