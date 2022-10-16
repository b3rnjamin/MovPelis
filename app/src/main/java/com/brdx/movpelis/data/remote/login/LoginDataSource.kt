package com.brdx.movpelis.data.remote.login

import com.brdx.movpelis.core.Resource

interface LoginDataSource {
    suspend fun signIn(username: String, password: String) : Resource<Boolean>
}