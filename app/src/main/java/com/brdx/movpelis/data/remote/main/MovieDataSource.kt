package com.brdx.movpelis.data.remote.main

import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.data.model.Movie

interface MovieDataSource {
    suspend fun listMovies(page: Int): Resource<List<Movie>>
}