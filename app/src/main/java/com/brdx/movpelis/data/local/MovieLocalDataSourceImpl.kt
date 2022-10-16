package com.brdx.movpelis.data.local

import com.brdx.movpelis.core.Utils.toMovieList
import com.brdx.movpelis.data.entity.MovieEntity
import com.brdx.movpelis.data.model.Movie

class MovieLocalDataSourceImpl(private val movieDao: MovieDao) {
    suspend fun getListMovies(): List<Movie> = movieDao.getListMovies().toMovieList()
    suspend fun saveMovie(movieEntity: MovieEntity) = movieDao.saveMovie(movieEntity)
}