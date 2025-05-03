package com.example.assigment1.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.assigment1.data.model.QuestionResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import com.example.assigment1.data.model.BookmarkedQuestionEntity
import com.example.assigment1.data.repository.ApiRepository
import com.example.assigment1.data.repository.BookmarkRepository
import com.google.gson.Gson

class QuizViewModel(
    application: Application,
    private val bookmarkRepository: BookmarkRepository,
    private val apiRepository: ApiRepository,
) : AndroidViewModel(application) {

    private val _questions = MutableStateFlow<List<QuestionResponse>>(emptyList())
    val questions: StateFlow<List<QuestionResponse>> = _questions

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    private val _selectedOptions = MutableStateFlow<Int?>(null)
    val selectedOptions: MutableStateFlow<Int?> = _selectedOptions

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished: StateFlow<Boolean> = _quizFinished

    private val _timePercent = MutableStateFlow(1.0f)
    val timePercent: StateFlow<Float> = _timePercent

    private val _correctAnswers = MutableStateFlow(0)
    val correctAnswers: StateFlow<Int> = _correctAnswers

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted

    private val _bookmarkedQuestions = MutableStateFlow<List<BookmarkedQuestionEntity>>(emptyList())
    val bookmarkedQuestions: StateFlow<List<BookmarkedQuestionEntity>> = _bookmarkedQuestions

    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds

    private val _timerDurationMillis = MutableStateFlow(30_000L)
    val timerDurationMillis: StateFlow<Long> = _timerDurationMillis

    private val isLoading = MutableStateFlow(true)

    private var timerJob: Job? = null

    private var mediaPlayer: MediaPlayer? = null
    private val _isMuted = mutableStateOf(false)
    val isMuted: State<Boolean> = _isMuted

    private var isBookmarkMode = false

    init {
        fetchQuestions()
        loadBookmarks()
    }

    // mute toggle handler
    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    // stop and start music
    fun stopAndReleaseMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // fetch questions from API
    private fun fetchQuestions() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiRepository.fetchQuestions()
                _questions.value = response
                startTimer()
            } catch (e: Exception) {
                isLoading.value = false
            }
        }
    }

    // select an option
    fun selectOption(index: Int) {
        _selectedOptions.value = index
    }

    // submit  button functionality makes the answer visible
    fun submit() {
        val selected = _selectedOptions.value
        val currentQuestion = _questions.value.getOrNull(_currentQuestionIndex.value)

        if (currentQuestion != null && selected != null) {
            if (selected == currentQuestion.correctOption - 1) {
                _correctAnswers.value++
            }
        }

        _isAnswerSubmitted.value = true
    }

    // skip button functionality
    fun skip() {
        nextQuestion()
    }

    // Load Next Question also checks if quiz is ended
    fun nextQuestion() {
        val questionsList = if (isBookmarkMode) _bookmarkedQuestions.value else _questions.value
        val nextIndex = _currentQuestionIndex.value + 1

        if (nextIndex < questionsList.size) {
            _currentQuestionIndex.value = nextIndex
            _selectedOptions.value = null
            startTimer()
        } else {
            _quizFinished.value = true
        }
        _isAnswerSubmitted.value = false
    }

    // Update timer duration
    fun updateTimerDuration(newDurationMillis: Long) {
        _timerDurationMillis.value = newDurationMillis
    }

    // Start Timer
    private fun startTimer() {
        timerJob?.cancel()
        _timePercent.value = 1.0f
        timerJob = viewModelScope.launch {
            val duration = _timerDurationMillis.value
            var remaining = duration
            val interval = 1_000L
            while (remaining > 0) {
                delay(interval)
                remaining -= interval
                _timePercent.value = remaining.toFloat() / duration
            }
            skip()
        }
    }

    // find the current bookmark
    fun bookmarkCurrent() {
        viewModelScope.launch {
            val currentQuestion = _questions.value.getOrNull(_currentQuestionIndex.value)
            currentQuestion?.let { question ->
                val entity = BookmarkedQuestionEntity(
                    questionId = question.uuidIdentifier,
                    questionText = question.question,
                    questionType = question.questionType,
                    option1 = question.option1,
                    option2 = question.option2,
                    option3 = question.option3,
                    option4 = question.option4,
                    correctOption = question.correctOption,
                    solutionJson = Gson().toJson(question.solution)
                )
                bookmarkRepository.addBookmark(entity)
                loadBookmarks()
            }
        }
    }

    // delete Bookmark
    fun deleteBookmark(questionId: String) {
        viewModelScope.launch {
            val entityToDelete = _bookmarkedQuestions.value.find { it.questionId == questionId }
            entityToDelete?.let {
                bookmarkRepository.deleteBookmark(it)
                loadBookmarks()
            }
        }
    }

    // load bookmarks from Database
    private fun loadBookmarks() {
        viewModelScope.launch {
            val bookmarks = bookmarkRepository.getAllBookmarks()
            _bookmarkedQuestions.value = bookmarks // <- No mapping
            _bookmarkedIds.value = bookmarks.map { it.questionId }.toSet()
        }
    }

    // reset all values
    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _selectedOptions.value = null
        _correctAnswers.value = 0
        _quizFinished.value = false
        _isAnswerSubmitted.value = false
        _timePercent.value = 1.0f
    }

    // stop timerJob when viewModel is cleared
    override fun onCleared() {
        timerJob?.cancel()
        stopAndReleaseMusic()
        super.onCleared()
    }
}
