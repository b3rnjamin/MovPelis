package com.brdx.movpelis.data.remote.login

import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.domain.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginDataSourceImpl(
    private val webService: WebService,
) : LoginDataSource {

    override suspend fun signIn(username: String, password: String): Resource<Boolean> =
        withContext(Dispatchers.IO) {
            if(username != "Admin"){
                throw Exception("Username incorrect")
            }

            if(password != "Password*123"){
                throw Exception("Password incorrect")
            }

            return@withContext Resource.Success(true)
        }
}