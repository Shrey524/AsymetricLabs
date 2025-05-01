package com.example.assigment1.data.repository

import com.example.assigment1.data.api.McqApiService
import com.example.assigment1.data.model.QuestionResponse

class QuestionRepository(private val api: McqApiService) {

    suspend fun fetchQuestions(): List<QuestionResponse> {
        return api.getQuestions()
    }
}