package com.brdx.movpelis.presentation.main

import androidx.lifecycle.*
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.data.model.Movie
import com.brdx.movpelis.domain.main.MovieRepo
import kotlinx.coroutines.Dispatchers

class MovieViewModel(private val repo: MovieRepo) : ViewModel() {

    val liveDataMovies = MutableLiveData<List<Movie>>(emptyList())

    fun setDataMovies(list: List<Movie>){
        liveDataMovies.value = list
    }

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

    fun saveMovie(movie: Movie) =
        liveData(viewModelScope.coroutineContext + Dispatchers.Main) {
            emit(Resource.Loading())
            try {
                emit(Resource.Success(repo.saveMovie(movie)))
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