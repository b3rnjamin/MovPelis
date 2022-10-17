package com.brdx.movpelis.core

import android.view.View
import com.brdx.movpelis.data.entity.MovieEntity
import com.brdx.movpelis.data.model.Movie

object Utils {
    fun List<MovieEntity>.toMovieList(): List<Movie> {
        val resultList = mutableListOf<Movie>()
        this.forEach { movieEntity ->
            resultList.add(movieEntity.toMovie())
        }
        return resultList
    }

    fun MovieEntity.toMovie(): Movie = Movie(
        this.id,
        this.poster_path,
        this.title,
        this.vote_average,
        this.release_date,
        this.overview
    )

    fun Movie.toMovieEntity(): MovieEntity = MovieEntity(
        this.id,
        this.poster_path,
        this.title,
        this.vote_average,
        this.release_date,
        this.overview
    )

    var View.visible: Boolean
        get() = visibility == View.VISIBLE
        set(value) {
            visibility = if (value) View.VISIBLE else View.GONE
        }
}