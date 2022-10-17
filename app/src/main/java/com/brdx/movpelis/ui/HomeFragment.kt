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
import com.brdx.movpelis.core.PaginationScrollListener
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
    private var isLastPage = false
    private var isLoading = false

    private val startPage = 1
    private var currentPage = startPage

    private lateinit var movieLayoutManager: GridLayoutManager
    private lateinit var onScrollListener : PaginationScrollListener

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

    private var isConnectionWithInternet = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding = FragmentHomeBinding.bind(view)

        connectivityObserver = NetworkConnectivityObserver(ctx)
        observeInternet()

        movieLayoutManager = GridLayoutManager(context, 2)
        onScrollListener = object : PaginationScrollListener(movieLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                if(isConnectionWithInternet){
                    loadNextPage()
                }else{
                    movieAdapter.removeLoadingFooter()
                }
            }

            override fun isLastPage(): Boolean = isLastPage
            override fun isLoading(): Boolean = isLoading
        }

        binding.rvMovies.apply {
            layoutManager = movieLayoutManager
            setHasFixedSize(true)
            adapter = movieAdapter
        }

    }

    private fun observeInternet() {
        connectivityObserver.observe().onEach { status ->

            viewModel.liveDataMovies.value = emptyList()
            currentPage = 1

            when (status) {
                ConnectivityObserver.Status.Available -> {
                    isConnectionWithInternet = true
                    observeMovies()
                }
                ConnectivityObserver.Status.Unavailable -> {
                    isConnectionWithInternet = false
                    observeLocalMovies()
                }
                ConnectivityObserver.Status.Losing -> {
                    isConnectionWithInternet = false
                    observeLocalMovies()
                }
                ConnectivityObserver.Status.Lost -> {
                    isConnectionWithInternet = false
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

                    if (movieList.isEmpty()) {
                        Snackbar.make(
                            binding.root,
                            "Don´t have movies in local storage",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
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
        viewModel.listMovies(currentPage).observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Snackbar.make(binding.root, "Loading from server...", Snackbar.LENGTH_SHORT)
                        .show()
                    movieAdapter.addLoadingFooter()
                }
                is Resource.Success -> {
                    movieAdapter.removeLoadingFooter()

                    currentPage = 1

                    val movieList = resource.data
                    viewModel.setDataMovies(movieList)
                    movieAdapter.setData(movieList)

                    binding.rvMovies.addOnScrollListener(onScrollListener)
                }
                is Resource.Failure -> {
                    movieAdapter.removeLoadingFooter()

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

    private fun loadNextPage() {
        viewModel.listMovies(currentPage).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    movieAdapter.addLoadingFooter()
                }
                is Resource.Success -> {
                    movieAdapter.removeLoadingFooter()

                    val notifications =
                        viewModel.liveDataMovies.value?.plus(result.data) ?: emptyList()
                    viewModel.setDataMovies(notifications)

                    isLastPage = result.data.isEmpty()

                    if (isLastPage) {
                        return@observe
                    }

                    isLoading = false
                    movieAdapter.setData(notifications)
                }
                is Resource.Failure -> {
                    movieAdapter.removeLoadingFooter()

                    val exception = result.exception
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