package com.example.mova.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mova.data.model.local.ListedContent
import com.example.mova.data.model.local.Movie
import com.example.mova.local.MovieDAO

@Database(entities = [Movie::class, ListedContent::class], version = 2)
abstract class MovieDataBase : RoomDatabase() {
    abstract fun getDao(): MovieDAO
}