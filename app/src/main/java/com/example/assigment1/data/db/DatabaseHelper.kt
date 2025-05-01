package com.example.assigment1.data.db

import com.example.assigment1.data.model.BookmarkedQuestionEntity

interface DatabaseHelper {
    suspend fun getCourses(): List<BookmarkedQuestionEntity>
    suspend fun insert(courses: List<BookmarkedQuestionEntity>)
    suspend fun deleteBookmark(course: BookmarkedQuestionEntity)
}