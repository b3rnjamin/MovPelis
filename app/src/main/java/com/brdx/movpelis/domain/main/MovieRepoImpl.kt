package com.brdx.movpelis.domain.main

import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.core.Utils.toMovieEntity
import com.brdx.movpelis.data.local.MovieLocalDataSourceImpl
import com.brdx.movpelis.data.model.Movie
import com.brdx.movpelis.data.remote.main.MovieDataSource

class MovieRepoImpl(
    private val dataSource: MovieDataSource,
    private val dataSourceLocal: MovieLocalDataSourceImpl
) : MovieRepo {
    override suspend fun listMovies(page: Int): Resource<List<Movie>> = dataSource.listMovies(page)

    override suspend fun listLocalMovies(): Resource<List<Movie>> =
        Resource.Success(dataSourceLocal.getListMovies())

    override suspend fun saveMovie(movie: Movie) = dataSourceLocal.saveMovie(movie.toMovieEntity())

}
