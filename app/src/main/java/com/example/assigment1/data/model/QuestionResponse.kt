package com.example.assigment1.data.model

data class QuestionResponse(
    val uuidIdentifier: String,
    val questionType: String,
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctOption: Int,
    val sort: Int,
    val solution: List<SolutionItem>
)

data class SolutionItem(
    val contentType: String,
    val contentData: String
)
