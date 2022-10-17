package com.brdx.movpelis.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.brdx.movpelis.R
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.core.RetrofitClient
import com.brdx.movpelis.data.local.AppDataBase
import com.brdx.movpelis.data.local.MovieLocalDataSourceImpl
import com.brdx.movpelis.data.model.Movie
import com.brdx.movpelis.data.remote.ConnectivityObserver
import com.brdx.movpelis.data.remote.NetworkConnectivityObserver
import com.brdx.movpelis.data.remote.main.MovieDataSourceImpl
import com.brdx.movpelis.databinding.FragmentHomeBinding
import com.brdx.movpelis.domain.main.MovieRepoImpl
import com.brdx.movpelis.presentation.main.MovieViewModel
import com.brdx.movpelis.presentation.main.MovieViewModelFactory
import com.brdx.movpelis.ui.adapter.MovieAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeFragment : Fragment(R.layout.fragment_home), MovieAdapter.OnMovieListener {

    private val ctx by lazy { requireActivity().applicationContext }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController

    private val movieAdapter = MovieAdapter(this)

    private val viewModel by activityViewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepoImpl(
                MovieDataSourceImpl(
                    RetrofitClient.webservice
                ),
                MovieLocalDataSourceImpl(AppDataBase.getDataBase(ctx).movieDao())
            )
        )
    }

    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding = FragmentHomeBinding.bind(view)

        connectivityObserver = NetworkConnectivityObserver(ctx)
        observeInternet()

        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = movieAdapter
        }
    }

    private fun observeInternet() {
        connectivityObserver.observe().onEach { status ->
            when (status) {
                ConnectivityObserver.Status.Available -> {
                    observeMovies()
                }
                ConnectivityObserver.Status.Unavailable -> {
                    observeLocalMovies()
                }
                ConnectivityObserver.Status.Losing -> {
                    observeLocalMovies()
                }
                ConnectivityObserver.Status.Lost -> {
                    observeLocalMovies()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)


    }

    private fun observeLocalMovies() {
        viewModel.listLocalMovies().observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Snackbar.make(binding.root, "Loading locally...", Snackbar.LENGTH_SHORT)
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
        viewModel.listMovies(1).observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Snackbar.make(binding.root, "Loading from server...", Snackbar.LENGTH_SHORT)
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
        val action =
            HomeFragmentDirections.actionHomeFragmentToMovieDetailFragment(
                movie = movie
            )

        navController.navigate(action)
    }
}