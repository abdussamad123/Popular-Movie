package com.popularmovie.appcomponent.data.remote

import com.popularmovie.appcomponent.domain.models.response.GenresResponse
import com.popularmovie.appcomponent.domain.models.response.MovieDetailsResponse
import com.popularmovie.appcomponent.domain.models.response.MovieResponse
import com.popularmovie.appcomponent.util.ApiConstants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AppLevelApi {

    @GET("${ApiConstants.MOVIE_API_ROUTE}/{sort_by}")
    suspend fun getMoviesAPi(
        @Path("sort_by") sortBy : String,
        @Query("api_key") apiKey : String,
        @Query("page") page : Int)
    : MovieResponse

    @GET(ApiConstants.GENRES_API_ROUTE)
    suspend fun getGenresApi( @Query("api_key") apiKey : String) : GenresResponse

    @GET("${ApiConstants.MOVIE_API_ROUTE}/{movieId}")
    suspend fun getMovieDetailsApi(@Path ("movieId") movieId : Long,
                                   @Query("api_key") apiKey : String) : MovieDetailsResponse

}