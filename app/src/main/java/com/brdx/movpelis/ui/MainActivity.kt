package com.brdx.movpelis.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.core.RetrofitClient
import com.brdx.movpelis.data.local.AppDataBase
import com.brdx.movpelis.data.local.MovieLocalDataSourceImpl
import com.brdx.movpelis.data.model.Movie
import com.brdx.movpelis.data.remote.ConnectivityObserver
import com.brdx.movpelis.data.remote.NetworkConnectivityObserver
import com.brdx.movpelis.data.remote.main.MovieDataSourceImpl
import com.brdx.movpelis.databinding.ActivityMainBinding
import com.brdx.movpelis.domain.main.MovieRepoImpl
import com.brdx.movpelis.presentation.main.MovieViewModel
import com.brdx.movpelis.presentation.main.MovieViewModelFactory
import com.brdx.movpelis.ui.adapter.MovieAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity(), MovieAdapter.OnMovieListener {
    private val context by lazy { this@MainActivity }

    private val movieAdapter = MovieAdapter(this)

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

    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        observeInternet()


        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = movieAdapter
        }

    }

    private fun observeInternet() {
        connectivityObserver.observe().onEach { status ->
            when(status){
                ConnectivityObserver.Status.Available -> observeMovies()
                ConnectivityObserver.Status.Unavailable -> observeLocalMovies()
                ConnectivityObserver.Status.Losing -> observeLocalMovies()
                ConnectivityObserver.Status.Lost -> observeLocalMovies()
            }
        }.launchIn(lifecycleScope)
    }

    private fun observeLocalMovies() {
        viewModel.listLocalMovies().observe(this) { resource ->
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
                    Snackbar.make(
                        binding.root,
                        (exception.message ?: "Error unexpected").toString(),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
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
                    Snackbar.make(
                        binding.root,
                        (exception.message ?: "Error unexpected").toString(),
                        Snackbar.LENGTH_SHORT
                    )
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