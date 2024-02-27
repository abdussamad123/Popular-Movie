package com.popularmovie.appcomponent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.popularmovie.appcomponent.data.local.entities.GenreEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface GenreDao {

    @Insert
    fun insert(genreEntity: GenreEntity)

    @Query("SELECT * from genreentity where genreId=:genreId")
    fun getGenre(genreId : Int) : Flow<GenreEntity>

    @Query("SELECT * from genreentity")
    fun getGenreList() : Flow<List<GenreEntity>>

    @Query("DELETE from genreentity")
    fun deleteAll()
}