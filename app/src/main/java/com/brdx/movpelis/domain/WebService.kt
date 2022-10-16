package com.brdx.movpelis.domain

import com.brdx.movpelis.BuildConfig

interface WebService {

    companion object {
        const val base_url: String = "https://api.themoviedb.org/3/movie/"
        const val api_key: String = "7e16622fd94b28d5121de5439fd2d5bf"

    }


}