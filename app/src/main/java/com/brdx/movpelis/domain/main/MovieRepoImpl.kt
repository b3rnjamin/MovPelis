package com.brdx.movpelis.domain.main

import android.util.Log
import com.brdx.movpelis.core.ConnectionError
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.data.model.Movie
import com.brdx.movpelis.domain.WebService
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.awaitResponse

class MovieRepoImpl(
    private val webService: WebService
) : MovieRepo {

    override suspend fun listMovies(page: Int): Resource<List<Movie>> =
        withContext(Dispatchers.IO) {

            debugLog("#Network: page=$page")
            val response: JsonElement?
            try {
                val responseBody = webService.getMovies(WebService.api_key, page).awaitResponse()
                response = getBodyFromResponse(responseBody)

                debugLog("getMovies: output: $response")
            } catch (e: Exception) { //HttpException
                debugLog("getMovies: onFailure: $e")
                throw ConnectionError.InternalApiError()
            }

            val movies = arrayListOf<Movie>()

            val jsonMovies = response.get("results").asJsonArray
            jsonMovies.forEach { jsonMovie ->
                val output = jsonMovie.asJsonObject

                val movie = Movie(
                    id = output.get("id").asString,
                    poster_path = "https://image.tmdb.org/t/p/w500" + output.get("poster_path").asString,
                    title = output.get("title").asString,
                    vote_average = output.get("vote_average").asDouble,
                    release_date = output.get("release_date").asString,
                    overview = output.get("overview").asString,
                )

                movies.add(movie)
            }
            return@withContext Resource.Success(movies)
        }

    private fun debugLog(message: String) {
        Log.d("#Network:", message)
    }

    private fun getBodyFromResponse(body: Response<JsonObject>): JsonObject =
        if (body.isSuccessful)
            body.body()!!
        else
            getErrorBodyFromResponse(body.toString())


    private fun getErrorBodyFromResponse(toJson: String): JsonObject =
        if (toJson.startsWith("{") && toJson.endsWith("}"))
            JsonParser.parseString(toJson).asJsonObject
        else {
            val body = JsonObject().apply {
                addProperty("resp_code", -1)
            }
            body
        }


}
