package com.popularmovie.appcomponent.domain.models.response

data class MovieDetailsResponse(
    val adult: Boolean,
    val original_title: String,
    val overview: String,
    val poster_path: String,
    val release_date:String,
    val vote_average: Double,
    val genres : List<GenresData>,
    val status: String

)

data class GenresData(val id : Int, val name:String )
