package com.popularmovie.appcomponent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.popularmovie.appcomponent.data.local.entities.MoviesGenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieGenresDao {

    @Insert
    fun insert(movieGenres : MoviesGenreEntity)

    @Query("DELETE from moviesgenreentity where sortBy =:sortBy")
    fun deleteAll(sortBy:String)

    @Query("SELECT DISTINCT * FROM moviesgenreentity WHERE genreId = :genreId")
    fun getMoviesBaseOnGenre(genreId:Int) : List<MoviesGenreEntity>
}