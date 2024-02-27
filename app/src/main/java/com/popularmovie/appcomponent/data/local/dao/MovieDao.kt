package com.popularmovie.appcomponent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.popularmovie.appcomponent.data.local.entities.MovieEntity
import com.popularmovie.appcomponent.data.local.entities.MovieWithGenres
import kotlinx.coroutines.flow.Flow


@Dao
interface MovieDao {

    @Query("SELECT * from movieentity where sortBy=:sortBy ORDER BY  id LIMIT :limit OFFSET :offset")
    fun getMovies(sortBy: String, limit:Int,offset:Int ): Flow<List<MovieEntity>>

    @Insert
    fun insertMovie(movieEntity: MovieEntity)

   /* @Query("SELECT MovieEntity.*, GROUP_CONCAT(DISTINCT MoviesGenreEntity.genreId) AS genre_ids FROM MovieEntity" +
                " LEFT JOIN MoviesGenreEntity ON MovieEntity.movieId = MoviesGenreEntity.movieId WHERE MovieEntity.movieId =:movieId" +
                " GROUP BY MovieEntity.movieId")
    fun getMovieDetails(movieId: Long): Flow<MovieWithGenres>*/

    @Query("DELETE from movieentity where sortBy=:sortBy")
    fun deleteAll(sortBy: String)

    @Query("SELECT DISTINCT * from movieentity where movieId=:movieId LIMIT 1")
    fun getMovie(movieId: Long) : MovieEntity
}