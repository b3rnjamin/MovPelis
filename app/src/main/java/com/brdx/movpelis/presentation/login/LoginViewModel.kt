package com.brdx.movpelis.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.domain.login.LoginRepo
import kotlinx.coroutines.Dispatchers

class LoginViewModel(private val repo: LoginRepo) : ViewModel() {

    fun signIn(username: String, password: String) =
        liveData(viewModelScope.coroutineContext + Dispatchers.Main) {
            emit(Resource.Loading())
            try {
                emit(repo.signIn(username, password))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
}

class LoginViewModelFactory(private val repo: LoginRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(LoginRepo::class.java).newInstance(repo)
    }
}