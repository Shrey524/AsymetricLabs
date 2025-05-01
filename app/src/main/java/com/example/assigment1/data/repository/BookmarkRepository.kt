package com.example.assigment1.data.repository

import com.example.assigment1.data.model.BookmarkedQuestionEntity

interface BookmarkRepository {
    suspend fun getAllBookmarks(): List<BookmarkedQuestionEntity>
    suspend fun addBookmark(bookmark: BookmarkedQuestionEntity)
    suspend fun deleteBookmark(bookmark: BookmarkedQuestionEntity)
}