package com.brdx.movpelis.domain.main

import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.data.model.Movie

interface MovieRepo {
    suspend fun listMovies(page: Int): Resource<List<Movie>>
}