package com.brdx.movpelis.domain.login

import com.brdx.movpelis.core.Resource

interface LoginRepo {
    suspend fun signIn(username: String, password: String) : Resource<Boolean>
}