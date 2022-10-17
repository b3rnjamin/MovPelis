package com.brdx.movpelis.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie_entity",
)
data class MovieEntity(
    @PrimaryKey
    val id: String = "",
    val poster_path: String = "",
    val title: String = "",
    val vote_average: Double = 0.0,
    val release_date: String = "",
    val overview: String = ""
)