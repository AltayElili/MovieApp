package com.example.movie.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Movie::class, ListedContent::class], version = 2)
abstract class MovieDataBase : RoomDatabase() {
    abstract fun getDao(): MovieDAO
}