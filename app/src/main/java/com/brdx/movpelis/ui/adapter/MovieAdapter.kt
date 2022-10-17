package com.brdx.movpelis.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.brdx.movpelis.R
import com.brdx.movpelis.core.BaseViewHolder
import com.brdx.movpelis.core.Utils.visible
import com.brdx.movpelis.data.model.Movie
import com.brdx.movpelis.databinding.ItemLoadingBinding
import com.brdx.movpelis.databinding.ItemMoviesBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class MovieAdapter(
    private val movieListener: OnMovieListener
) : RecyclerView.Adapter<BaseViewHolder<Movie>>() {


    private val LOADING = 0
    private val ITEM = 1

    var isLoadingAdded = false

    private var movieList = arrayListOf<Movie>()

    fun setData(list: List<Movie>) {
        val diffUtil = ContactDiffUtil(movieList, list)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        movieList.clear()
        movieList.addAll(list)
        diffResults.dispatchUpdatesTo(this)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        movieList.add(Movie())
        notifyItemInserted(movieList.size - 1)
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = movieList.size - 1
        movieList.removeAt(position)
        notifyItemRemoved(position)
    }


    interface OnMovieListener {
        fun onMovieCLickListener(movie: Movie, position: Int)
    }

    override fun getItemCount(): Int = movieList.size

    override fun getItemViewType(position: Int): Int =
        if ((position == movieList.size - 1) && isLoadingAdded) LOADING else ITEM

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Movie> {
        return when (viewType) {
            ITEM -> {
                val binding = ItemMoviesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = MovieViewHolder(binding, parent.context)
                binding.movieCover.setOnClickListener {
                    val position = holder.adapterPosition.takeIf {
                        it != DiffUtil.DiffResult.NO_POSITION
                    } ?: return@setOnClickListener
                    movieListener.onMovieCLickListener(movieList[position], position)
                }
                holder
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = LoadingViewHolder(binding, parent.context)

                holder
            }
            else -> throw IllegalArgumentException("Not View Available")
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<Movie>, position: Int) {
        when (holder) {
            is MovieViewHolder -> holder.bind(movieList[position], position)
            is LoadingViewHolder -> holder.bind(movieList[position], position)
            else -> throw IllegalArgumentException("Not View Available")
        }
    }

    private inner class MovieViewHolder(
        private val binding: ItemMoviesBinding,
        private val context: Context
    ) :
        BaseViewHolder<Movie>(binding.root) {

        private val requestOptions =
            RequestOptions().error(R.drawable.ic_error).placeholder(R.drawable.ic_logo)

        override fun bind(item: Movie, position: Int) {

            binding.title.text = item.title

            Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(item.poster_path)
                .into(binding.imageMovieCover)
        }
    }

    inner class LoadingViewHolder(
        private val binding: ItemLoadingBinding,
        private val context: Context
    ) : BaseViewHolder<Movie>(binding.root) {

        override fun bind(item: Movie, position: Int) {
            binding.loadMoreProgress.visible = true
        }
    }

    inner class ContactDiffUtil(
        private val oldList: List<Movie>,
        private val newList: List<Movie>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

}