package com.brdx.movpelis.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brdx.movpelis.data.entity.MovieEntity

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie_entity")
    suspend fun getListMovies(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovie(movie: MovieEntity)
}