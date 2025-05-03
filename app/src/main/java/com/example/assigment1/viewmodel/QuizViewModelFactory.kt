package com.example.assigment1.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assigment1.data.repository.ApiRepository
import com.example.assigment1.data.repository.BookmarkRepository

class QuizViewModelFactory(
    private val application: Application,
    private val bookmarkRepository: BookmarkRepository,
    private val apiRepository: ApiRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(application, bookmarkRepository, apiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}