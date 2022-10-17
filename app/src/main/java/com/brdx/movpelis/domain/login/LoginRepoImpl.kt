package com.brdx.movpelis.domain.login

import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.data.remote.login.LoginDataSource

class LoginRepoImpl(
    private val dataSourceRemote: LoginDataSource,
) : LoginRepo {

    override suspend fun signIn(username: String, password: String): Resource<Boolean> =
        dataSourceRemote.signIn(username, password)

}
