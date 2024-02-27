package com.popularmovie.appcomponent.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MoviesGenreEntity(
    @PrimaryKey
    val id: Int?,
    val movieId: Long,
    val genreId: Int,
    val sortBy : String
)
