package com.example.assigment1.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.assigment1.R
import com.example.assigment1.data.api.McqApiService
import com.example.assigment1.data.model.QuestionResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.assigment1.data.model.BookmarkedQuestionEntity
import com.example.assigment1.data.repository.BookmarkRepository
import com.google.gson.Gson

class QuizViewModel
    (application: Application,
     private val bookmarkRepository: BookmarkRepository
) : AndroidViewModel(application)
{
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

    private val isLoading = MutableStateFlow(true)

    private var timerJob: Job? = null

    private var mediaPlayer: MediaPlayer? = null
    private val _isMuted = mutableStateOf(false)
    val isMuted: State<Boolean> = _isMuted

    init {
        initMusic()
        fetchQuestions()
        loadBookmarks()
    }

    private fun initMusic() {
        mediaPlayer = MediaPlayer.create(getApplication(), R.raw.bg_music)
        mediaPlayer?.isLooping = true
    }

    fun startMusic() {
        if (_isMuted.value) return
        mediaPlayer?.start()
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        if (_isMuted.value) {
            pauseMusic()
        } else {
            startMusic()
        }
    }

    fun stopAndReleaseMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun fetchQuestions() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = McqApiService.create().getQuestions()
                _questions.value = response
                startTimer()
            } catch (e: Exception) {
                // handle error
            } finally {
                isLoading.value = false
            }
        }
    }

    fun selectOption(index: Int) {
        _selectedOptions.value = index
    }

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

    fun skip(isBookmarkMode: Boolean) {
        nextQuestion(isBookmarkMode)
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    fun nextQuestion(isBookmarkMode: Boolean) {
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


    private fun startTimer() {
        timerJob?.cancel()
        _timePercent.value = 1.0f
        timerJob = viewModelScope.launch {
            val totalTime = 60_000L
            val interval = 1_000L
            var remaining = totalTime
            while (remaining > 0) {
                delay(interval)
                remaining -= interval
                _timePercent.value = remaining.toFloat() / totalTime
            }
            submit()
        }
    }

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

    fun deleteBookmark(questionId: String) {
        viewModelScope.launch {
            val entityToDelete = _bookmarkedQuestions.value.find { it.questionId == questionId }
            entityToDelete?.let {
                bookmarkRepository.deleteBookmark(it)
                loadBookmarks() // 👈 refresh state
            }
        }
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            val bookmarks = bookmarkRepository.getAllBookmarks()
            _bookmarkedQuestions.value = bookmarks // <- No mapping
            _bookmarkedIds.value = bookmarks.map { it.questionId }.toSet()
        }
    }

    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _selectedOptions.value = null
        _correctAnswers.value = 0
        _quizFinished.value = false
        _isAnswerSubmitted.value = false
        _timePercent.value = 1.0f
    }

    override fun onCleared() {
        timerJob?.cancel()
        stopAndReleaseMusic()
        super.onCleared()
    }
}
