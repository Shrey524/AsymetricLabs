package com.example.assigment1.data.repository

import com.example.assigment1.data.db.BookmarkedDao
import com.example.assigment1.data.model.BookmarkedQuestionEntity

class BookmarkRepositoryImpl(private val bookmarkDao: BookmarkedDao) : BookmarkRepository {
    override suspend fun getAllBookmarks(): List<BookmarkedQuestionEntity> {
        return bookmarkDao.getAllBookmarks()
    }

    override suspend fun addBookmark(bookmark: BookmarkedQuestionEntity) {
        bookmarkDao.insertBookmark(bookmark)
    }

    override suspend fun deleteBookmark(bookmark: BookmarkedQuestionEntity) {
        bookmarkDao.deleteBookmark(bookmark)
    }
}
