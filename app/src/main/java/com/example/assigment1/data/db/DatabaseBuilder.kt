package com.example.assigment1.data.db

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {

    private var INSTANCE: BookmarkDatabase? = null
    fun getInstance(context: Context): BookmarkDatabase {
        if (INSTANCE == null) {
            synchronized(BookmarkDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }
    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            BookmarkDatabase::class.java,
            "bookmarked questions"
        ).build()
}