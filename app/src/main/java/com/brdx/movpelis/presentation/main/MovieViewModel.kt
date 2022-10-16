package com.brdx.movpelis.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.domain.main.MovieRepo
import kotlinx.coroutines.Dispatchers

class MovieViewModel(private val repo: MovieRepo) : ViewModel() {

    fun listMovies(page: Int) =
        liveData(viewModelScope.coroutineContext + Dispatchers.Main) {
            emit(Resource.Loading())
            try {
                emit(repo.listMovies(page))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }

    fun listLocalMovies() =
        liveData(viewModelScope.coroutineContext + Dispatchers.Main) {
            emit(Resource.Loading())
            try {
                emit(repo.listLocalMovies())
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}

class MovieViewModelFactory(private val repo: MovieRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MovieRepo::class.java).newInstance(repo)
    }
}