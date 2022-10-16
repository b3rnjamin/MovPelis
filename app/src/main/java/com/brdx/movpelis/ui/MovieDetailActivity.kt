package com.brdx.movpelis.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.brdx.movpelis.R

class MovieDetailActivity : AppCompatActivity() {

    /**
     * La pantalla principal con el viewpager deberia ser en un fragmnet y el detalle de la pelicula
     * deberia ser un fragment tambien, ambos contenidos dentro del mainactivity con una unica
     * instancia del viewmodel que compartirian mediante "by activityViewModels"
     * **/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
    }
}