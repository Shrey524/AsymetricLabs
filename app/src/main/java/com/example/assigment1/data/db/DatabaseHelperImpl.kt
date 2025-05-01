package com.example.assigment1.data.db

import com.example.assigment1.data.model.BookmarkedQuestionEntity

class DatabaseHelperImpl(private val bookmarkDatabase: BookmarkDatabase) : DatabaseHelper {

    override suspend fun getCourses(): List<BookmarkedQuestionEntity> =
        bookmarkDatabase.bookmarkDao().getAllBookmarks()

    override suspend fun insert(courses: List<BookmarkedQuestionEntity>) {
        courses.forEach { course ->
            bookmarkDatabase.bookmarkDao().insertBookmark(course)
        }
    }

    override suspend fun deleteBookmark(course: BookmarkedQuestionEntity) {
        bookmarkDatabase.bookmarkDao().deleteBookmark(course)
    }
}
