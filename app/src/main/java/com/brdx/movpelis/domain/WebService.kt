package com.brdx.movpelis.domain

import com.brdx.movpelis.BuildConfig
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WebService {

    companion object {
        const val base_url: String = "https://api.themoviedb.org/3/movie/"
        const val api_key: String = "7e16622fd94b28d5121de5439fd2d5bf"
    }

    //https://api.themoviedb.org/3/movie/upcoming?page=1&api_key=f46b58478f489737ad5a4651a4b25079

    @GET("upcoming")
    suspend fun getMovies(
        @Path("page") page: Int,
        @Path("api_key") api_key: String
    ): Call<JsonObject>

}