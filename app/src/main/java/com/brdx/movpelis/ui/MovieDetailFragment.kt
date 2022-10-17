package com.brdx.movpelis.ui

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.brdx.movpelis.R
import com.brdx.movpelis.core.Resource
import com.brdx.movpelis.core.RetrofitClient
import com.brdx.movpelis.data.local.AppDataBase
import com.brdx.movpelis.data.local.MovieLocalDataSourceImpl
import com.brdx.movpelis.data.model.Movie
import com.brdx.movpelis.data.remote.main.MovieDataSourceImpl
import com.brdx.movpelis.databinding.FragmentMovieDetailBinding
import com.brdx.movpelis.domain.main.MovieRepoImpl
import com.brdx.movpelis.presentation.main.MovieViewModel
import com.brdx.movpelis.presentation.main.MovieViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar


class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {

    private val ctx by lazy { requireActivity().applicationContext }

    private lateinit var binding: FragmentMovieDetailBinding
    private lateinit var navController: NavController
    private val args by navArgs<MovieDetailFragmentArgs>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding = FragmentMovieDetailBinding.bind(view)

        //addOnBackPressedCallback()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val movie = args.movie
        binding.detailMovieTitle.text = movie.title
        binding.detailMovieReleaseDate.text = movie.release_date
        binding.detailMovieScore.text = movie.vote_average.toString()
        binding.detailMovieDesc.text = movie.overview

        val requestOptions =
            RequestOptions().error(R.drawable.ic_error).placeholder(R.drawable.ic_logo)

        Glide.with(ctx)
            .setDefaultRequestOptions(requestOptions)
            .load(movie.poster_path)
            .into(binding.detailMovieCover)


        Glide.with(ctx)
            .setDefaultRequestOptions(requestOptions)
            .load(movie.poster_path)
            .into(binding.detailMovieImg)

        observeSaveMovie(movie)
    }

    private fun observeSaveMovie(movie: Movie) {
        viewModel.saveMovie(movie).observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Snackbar.make(
                        binding.root,
                        "Saving to watch movie without internet",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Saved correctly", Snackbar.LENGTH_SHORT)
                        .show()
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


    private fun addOnBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            navController.navigate(
                R.id.homeFragment,
                null
            )
        }
    }


}