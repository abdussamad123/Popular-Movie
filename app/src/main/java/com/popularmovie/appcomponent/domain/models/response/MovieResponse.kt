package com.popularmovie.appcomponent.domain.models.response

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("page")
    val page:Int,
    @SerializedName("total_pages")
    val totalPages:Int,
    @SerializedName("total_results")
    val totalResults:Int,
    val results : ArrayList<MovieData>


    )

data class MovieData(
    @SerializedName("adult")
    val isForAdult:Boolean,
    @SerializedName("backdrop_path")
    val backdropPath:Boolean,
    @SerializedName("id")
    val movieId:Long,
    @SerializedName("original_language")
    val originalLanguage:String,
    @SerializedName("original_title")
    val originalTitle:String,
    @SerializedName("overview")
    val overview:String,
    @SerializedName("popularity")
    val popularity:Double,
    @SerializedName("poster_path")
    val posterPath:String,
    @SerializedName("release_date")
    val releaseDate:String,
    @SerializedName("title")
    val title:String,
    @SerializedName("video")
    val hasVideo:Boolean,
    @SerializedName("vote_average")
    val rating:Float,
    @SerializedName("vote_count")
    val voteCount:Int,
    @SerializedName("genre_ids")
    val genreIds : ArrayList<Int>
    )