package com.brdx.movpelis.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.brdx.movpelis.core.RetrofitClient
import com.brdx.movpelis.data.local.AppDataBase
import com.brdx.movpelis.data.local.MovieLocalDataSourceImpl
import com.brdx.movpelis.data.remote.main.MovieDataSourceImpl
import com.brdx.movpelis.databinding.ActivityMainBinding
import com.brdx.movpelis.domain.main.MovieRepoImpl
import com.brdx.movpelis.presentation.main.MovieViewModel
import com.brdx.movpelis.presentation.main.MovieViewModelFactory

class MainActivity : AppCompatActivity() {
    private val context by lazy { this@MainActivity }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepoImpl(
                MovieDataSourceImpl(
                    RetrofitClient.webservice
                ),
                MovieLocalDataSourceImpl(AppDataBase.getDataBase(applicationContext).movieDao())
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}