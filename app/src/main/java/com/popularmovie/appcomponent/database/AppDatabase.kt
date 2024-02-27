package com.popularmovie.appcomponent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.popularmovie.appcomponent.data.local.dao.GenreDao
import com.popularmovie.appcomponent.data.local.dao.MovieDao
import com.popularmovie.appcomponent.data.local.dao.MovieGenresDao
import com.popularmovie.appcomponent.data.local.entities.GenreEntity
import com.popularmovie.appcomponent.data.local.entities.MovieEntity
import com.popularmovie.appcomponent.data.local.entities.MoviesGenreEntity


@Database(entities = [MovieEntity::class, GenreEntity::class, MoviesGenreEntity::class],
    version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getMovieDao(): MovieDao
    abstract fun getGenreDao(): GenreDao
    abstract fun getMovieGenres() : MovieGenresDao

}