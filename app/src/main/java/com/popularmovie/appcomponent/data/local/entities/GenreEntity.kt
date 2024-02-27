package com.popularmovie.appcomponent.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GenreEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val genreId : Int,
    val name : String
)
