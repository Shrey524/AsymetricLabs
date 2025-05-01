package com.example.assigment1.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.assigment1.data.model.BookmarkedQuestionEntity

@Database(
    entities = [BookmarkedQuestionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BookmarkDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkedDao

    companion object {
        @Volatile
        private var INSTANCE: BookmarkDatabase? = null

        fun getDatabase(context: Context): BookmarkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookmarkDatabase::class.java,
                    "bookmark_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
