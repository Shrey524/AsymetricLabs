package com.example.assigment1.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assigment1.data.model.BookmarkedQuestionEntity

@Dao
interface BookmarkedDao {

    @Query("SELECT * FROM bookmarked_questions")
    suspend fun getAllBookmarks(): List<BookmarkedQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmarkedQuestionEntity: BookmarkedQuestionEntity)

    @Delete
    suspend fun deleteBookmark(bookmarkedQuestionEntity: BookmarkedQuestionEntity)
}