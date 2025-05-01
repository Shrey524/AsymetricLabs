package com.example.assigment1.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_questions")
data class BookmarkedQuestionEntity(
    @PrimaryKey val questionId: String,
    @ColumnInfo(name = "questionText")val questionText: String,
    @ColumnInfo(name = "questionType")val questionType: String,
    @ColumnInfo(name = "option1")val option1: String,
    @ColumnInfo(name = "option2")val option2: String,
    @ColumnInfo(name = "option3")val option3: String,
    @ColumnInfo(name = "option4")val option4: String,
    @ColumnInfo(name = "correctOption")val correctOption: Int,
    @ColumnInfo(name = "solutionJson")val solutionJson: String
)
