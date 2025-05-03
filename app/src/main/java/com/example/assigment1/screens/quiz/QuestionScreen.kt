package com.example.assigment1.screens.quiz

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
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
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    navController: NavHostController,
    viewModel: QuizViewModel = viewModel(),
    onCloseClick: () -> Unit,
    onQuizFinished: (correctAnswers: Int, totalQuestions: Int) -> Unit,
    isBookmarkMode: Boolean = false
) {
    // variables for timer, background music, bookmark and end of quiz
    val isMuted by viewModel.isMuted
    val timeRemainingPercent by viewModel.timePercent.collectAsState()
    val quizFinished by viewModel.quizFinished.collectAsState()
    val bookmarked = viewModel.bookmarkedQuestions.collectAsState().value

    // unique id to identify bookmarks
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    // variables for question number, answer selected, answer submitted
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOptions.collectAsState()
    val isAnswerSubmitted by viewModel.isAnswerSubmitted.collectAsState()

    // variables storing list of questions/ current question and options
    val apiQuestions = viewModel.questions.collectAsState().value
    val questions = rememberUpdatedState(
        if (isBookmarkMode) bookmarked.map { it.toQuestionResponse() } else apiQuestions
    ).value
    val currentQuestion = questions.getOrNull(currentIndex)
    val options = currentQuestion?.let {
        listOf(
            it.option1,
            it.option2,
            it.option3,
            it.option4)
    }

    // flag to check if a question is bookmarked or not
    val isBookmarked = currentQuestion?.uuidIdentifier?.let { bookmarkedIds.contains(it) } ?: false

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Declare mediaPlayer
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.bg_music).apply {
            isLooping = true
        }
    }

    // start and stop music
    LaunchedEffect(isMuted) {
        if (isMuted) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (!isMuted) mediaPlayer.start()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mediaPlayer.pause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            mediaPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Loading Screen till the and api response is loaded
    if (currentQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading question...", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // QuizScreen Header (Linear ProgressBar + Mute/UnMute + Close)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuestionScreenTopBar(
                timeRemainingPercent = timeRemainingPercent,
                isMuted = isMuted,
                onToggleMute = { viewModel.toggleMute() },
                onClose = {
                    viewModel.stopAndReleaseMusic()
                    if (isBookmarkMode) onCloseClick() else navController.navigate("home")
                }
            )
        }

        //Space
        Spacer(modifier = Modifier.height(16.dp))

        //Question Number
        Text("Question ${currentIndex + 1}", style = MaterialTheme.typography.titleMedium)

        //Space
        Spacer(modifier = Modifier.height(8.dp))

        //Question
        QuestionContent(type = currentQuestion.questionType, content = currentQuestion.question)

        //Space
        Spacer(modifier = Modifier.height(24.dp))

        // Options
        QuestionOptions(
            options = options,
            selectedOption = selectedOption,
            isAnswerSubmitted = isAnswerSubmitted,
            onOptionSelected = { viewModel.selectOption(it) }
        )

        //Space
        Spacer(modifier = Modifier.weight(1f))

        //Answer Explanation only visible after answer is submitted
        if (isAnswerSubmitted) {
            AnswerExplanation(
                isCorrect = selectedOption == (currentQuestion.correctOption - 1),
                solution = currentQuestion.solution
            )
        }

        // QuizScreen Footer (Bookmark + Next/Skip + Submit)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuizScreenFooter(
                isBookmarked = isBookmarked,
                isAnswerSubmitted = isAnswerSubmitted,
                selectedOption = selectedOption,
                isBookmarkMode = isBookmarkMode,
                onBookmarkClick = {
                    currentQuestion.uuidIdentifier.let { id ->
                        if (isBookmarked) viewModel.deleteBookmark(id)
                        else viewModel.bookmarkCurrent()
                    }
                },
                onSkipOrNext = {
                    if (isAnswerSubmitted) viewModel.nextQuestion()
                    else viewModel.skip()
                },
                onSubmit = { viewModel.submit() },
                context = LocalContext.current,
                currentQuestion = currentQuestion
            )
        }
    }

    //  navigate to result screen
    LaunchedEffect(quizFinished) {
        if (quizFinished) {
            viewModel.stopAndReleaseMusic()
            onQuizFinished(viewModel.correctAnswers.value, questions.size)
        }
    }
}

@Composable
fun QuestionScreenTopBar(
    timeRemainingPercent: Float,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LinearProgressIndicator(
            progress = timeRemainingPercent,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(8.dp),
            trackColor = Color.LightGray,
            color = Color.DarkGray
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onToggleMute) {
                Icon(
                    painter = painterResource(id = if (isMuted) R.drawable.ic_mute else R.drawable.ic_unmute),
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun QuestionOptions(
    options: List<String>?,
    selectedOption: Int?,
    isAnswerSubmitted: Boolean,
    onOptionSelected: (Int) -> Unit
) {
    options?.forEachIndexed { index, option ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isAnswerSubmitted) { onOptionSelected(index) }
                .padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = selectedOption == index,
                onClick = if (!isAnswerSubmitted) { { onOptionSelected(index) } } else null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Black,
                    unselectedColor = Color.DarkGray
                )
            )
            Text(
                text = option,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodyMedium

            )
        }
    }
}

@Composable
fun QuizScreenFooter(
    isBookmarked: Boolean,
    isAnswerSubmitted: Boolean,
    selectedOption: Int?,
    isBookmarkMode: Boolean,
    onBookmarkClick: () -> Unit,
    onSkipOrNext: () -> Unit,
    onSubmit: () -> Unit,
    context: Context,
    currentQuestion : QuestionResponse?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bookmark functionality
        Icon(
            painter = painterResource(id = if (isBookmarked) R.drawable.ic_bookmarked else R.drawable.ic_bookmark),
            contentDescription = if (isBookmarked) "Bookmarked" else "Bookmark",
            modifier = Modifier
                .size(24.dp)
                .clickable(enabled = !isAnswerSubmitted && !isBookmarkMode) {
                    onBookmarkClick()
                }
        )

        // Share Icon
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share Question",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, currentQuestion?.question)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share question via"))                }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {

            // Text button for Next/Skip
            TextButton(onClick = onSkipOrNext) {
                Text(
                    if (isAnswerSubmitted) "Next" else "Skip",
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Button to Submit Answer only active before the answer is submitted
            Button(
                onClick = onSubmit,
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

@Composable
fun QuestionContent(
    type: String,
    content: String
) {
    when (type) {
        "text" -> {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        "htmlText" -> {
            AndroidView(
                factory = { context -> TextView(context).apply { textSize = 18f } },
                update = {
                    it.text = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        "image" -> {
            AsyncImage(
                model = content,
                contentDescription = "Question Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        else -> {
            Text(
                text = "Unsupported question type.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AnswerExplanation(
    isCorrect: Boolean,
    solution: List<SolutionItem>
) {
    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = if (isCorrect) "Correct Answer!" else "Wrong Answer!",
        modifier = Modifier.padding(vertical = 8.dp),
        color = if (isCorrect) Color.Green else Color.Red,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )

    Text("Explanation:", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(4.dp))

    solution.forEach { item ->
        when (item.contentType) {
            "text" -> Text(item.contentData, style = MaterialTheme.typography.bodyMedium)

            "htmlText" -> AndroidView(
                factory = { context -> TextView(context).apply { textSize = 16f } },
                update = {
                    it.text = HtmlCompat.fromHtml(item.contentData, HtmlCompat.FROM_HTML_MODE_COMPACT)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            "image" -> AsyncImage(
                model = item.contentData,
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
