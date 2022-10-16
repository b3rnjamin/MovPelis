package com.brdx.movpelis.domain.login

import androidx.core.util.Pair
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.domain.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepoImpl(
    private val webService: WebService
) : LoginRepo {
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
