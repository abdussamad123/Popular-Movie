package com.popularmovie.appcomponent.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.coroutines.flow.Flow

@Entity
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val isForAdult:Boolean,
    val backdropPath:Boolean,
    val movieId:Long,
    val originalLanguage:String,
    val originalTitle:String,
    val overview:String,
    val popularity:Double,
    val posterPath:String,
    val releaseDate:String,
    val title:String,
    val hasVideo:Boolean,
    val rating:Float,
    val voteCount:Int,
    val sortBy:String
)

data class  MovieWithGenres(
    @Embedded
    val movieEntity: MovieEntity,
    val genre_ids : String
)