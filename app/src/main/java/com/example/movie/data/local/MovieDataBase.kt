package com.example.movie.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movie.data.local.MovieDAO
import com.example.movie.data.model.local.ListedContent
import com.example.movie.data.model.local.Movie

@Database(entities = [Movie::class, ListedContent::class], version = 2)
abstract class MovieDataBase : RoomDatabase() {
    abstract fun getDao(): MovieDAO
}