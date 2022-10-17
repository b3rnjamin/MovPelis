package com.brdx.movpelis.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: String = "",
    val poster_path: String = "",
    val title: String = "",
    val vote_average: Double = 0.0,
    val release_date: String = "",
    val overview: String = ""
) : Parcelable