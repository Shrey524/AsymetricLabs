package com.example.assigment1.data.repository

import com.example.assigment1.data.api.ApiServiceProvider
import com.example.assigment1.data.model.QuestionResponse

class ApiRepository {

    private val mcqApi = ApiServiceProvider.mcqApiService

    suspend fun fetchQuestions(): List<QuestionResponse> {
        return mcqApi.getQuestions()
    }
}
