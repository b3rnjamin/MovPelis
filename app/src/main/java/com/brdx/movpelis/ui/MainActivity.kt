package com.brdx.movpelis.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.core.RetrofitClient
import com.brdx.movpelis.data.model.Movie
import com.brdx.movpelis.databinding.ActivityMainBinding
import com.brdx.movpelis.domain.login.LoginRepoImpl
import com.brdx.movpelis.domain.main.MovieRepoImpl
import com.brdx.movpelis.presentation.login.LoginViewModel
import com.brdx.movpelis.presentation.login.LoginViewModelFactory
import com.brdx.movpelis.presentation.main.MovieViewModel
import com.brdx.movpelis.presentation.main.MovieViewModelFactory
import com.brdx.movpelis.ui.adapter.MovieAdapter
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), MovieAdapter.OnMovieListener {
    private val context by lazy { this@MainActivity }

    private val movieAdapter = MovieAdapter(this)

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepoImpl(
                RetrofitClient.webservice
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(context, 4)
            setHasFixedSize(true)
            adapter = movieAdapter
        }

        observeMovies()
    }

    private fun observeMovies() {
        viewModel.listMovies(1).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Snackbar.make(binding.root, "Loading...", Snackbar.LENGTH_SHORT)
                        .show()
                }
                is Resource.Success -> {
                    val movieList = resource.data
                    movieAdapter.setData(movieList)
                }
                is Resource.Failure -> {
                    val exception = resource.exception
                    Snackbar.make(binding.root, (exception.message ?: "Error unexpected").toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onMovieCLickListener(movie: Movie, position: Int) {
        val intent = Intent(context, MovieDetailActivity::class.java)
        startActivity(intent)
        finish()
    }


}