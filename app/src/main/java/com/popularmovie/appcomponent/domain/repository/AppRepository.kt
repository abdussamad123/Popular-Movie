package com.popularmovie.appcomponent.domain.repository

import com.popularmovie.BuildConfig
import com.popularmovie.appcomponent.data.local.entities.GenreEntity
import com.popularmovie.appcomponent.data.local.entities.MovieEntity
import com.popularmovie.appcomponent.data.local.entities.MoviesGenreEntity
import com.popularmovie.appcomponent.data.remote.AppLevelApi
import com.popularmovie.appcomponent.database.AppDatabase
import com.popularmovie.appcomponent.di.AppModule
import com.popularmovie.appcomponent.domain.models.response.Genre
import com.popularmovie.appcomponent.util.networkBoundResource
import com.popularmovie.appcomponent.util.networkBoundResourceWithoutDb
import javax.inject.Inject

class AppRepository@Inject constructor(
    @AppModule.AuthAppLevelApi private val appLevelApi: AppLevelApi,
    private val db : AppDatabase
) {

    private val movieDao = db.getMovieDao()
    private val genreDao = db.getGenreDao()
    private val movieGenresDao = db.getMovieGenres()

    suspend fun getMovies(sortBy:String,limit : Int, offset : Int,page:Int) = networkBoundResource(
        query = {
            db.getMovieDao().getMovies(sortBy,limit, offset)
        },
        fetch = {
            appLevelApi.getMoviesAPi(sortBy,BuildConfig.MOVIE_DB_API_KEY, page = page+1)
        },
        saveFetchResult = {
            movieDao.deleteAll(sortBy)
            movieGenresDao.deleteAll(sortBy)

            it.results.forEach { movieData ->
                movieDao.insertMovie(
                    MovieEntity(
                        null,
                        movieData.isForAdult,
                        movieData.backdropPath,
                        movieData.movieId,
                        movieData.originalLanguage,
                        movieData.originalTitle,
                        movieData.overview,
                        movieData.popularity,
                        movieData.posterPath,
                        movieData.releaseDate,
                        movieData.title,
                        movieData.hasVideo,
                        movieData.rating,
                        movieData.voteCount,
                        sortBy
                    )
                )
                movieData.genreIds.forEach {
                    movieGenresDao.insert(MoviesGenreEntity(null,movieData.movieId,it,sortBy))
                }

            }
        },
        shouldFetch = {
            it.isEmpty()
        }
    )

    suspend fun getGenresList() = networkBoundResource(
        query = {
            db.getGenreDao().getGenreList()
        },
        fetch = {
         appLevelApi.getGenresApi(BuildConfig.MOVIE_DB_API_KEY)
        },
        saveFetchResult = {
            genreDao.deleteAll()
            it.genres.forEach {genre: Genre ->
              genreDao.insert(GenreEntity(null,genre.id,genre.name))
            }
        },
        shouldFetch = {
            it.isEmpty()
        }
    )

    fun getGenre(genreId : Int) = genreDao.getGenre(genreId)
    fun getMoviesBaseOnGenre(genreId: Int) = movieGenresDao.getMoviesBaseOnGenre(genreId)
    fun getMovie(movieId: Long) = movieDao.getMovie(movieId)

    fun getMovieDetailsApi(movieId: Long) = networkBoundResourceWithoutDb {
        appLevelApi.getMovieDetailsApi(movieId,BuildConfig.MOVIE_DB_API_KEY)
    }
}

