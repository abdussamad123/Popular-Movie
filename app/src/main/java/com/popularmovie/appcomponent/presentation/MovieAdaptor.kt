package com.popularmovie.appcomponent.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.popularmovie.appcomponent.data.local.entities.MovieEntity
import com.popularmovie.appcomponent.util.loadImage
import com.popularmovie.databinding.ItemMovieBinding

class MovieAdaptor(private val movieClickListener:OnMovieClickListener) : RecyclerView.Adapter<MovieAdaptor.MovieAdaptorVH>() {

    private var moviesList = ArrayList<MovieEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdaptorVH {
        return MovieAdaptorVH(ItemMovieBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    override fun onBindViewHolder(holder: MovieAdaptorVH, position: Int) {
       holder.bindData(moviesList[position])
    }

    fun setData(data: List<MovieEntity>){
        moviesList.addAll(data)
        notifyDataSetChanged()
    }

    fun clearData(){
        moviesList.clear()
        notifyDataSetChanged()
    }

    inner class MovieAdaptorVH(private val movieItem : ItemMovieBinding) : RecyclerView.ViewHolder(movieItem.root){
        fun bindData(movieData : MovieEntity){

            movieItem.tvOriginalTitle.text = movieData.originalTitle

            "IMDb ${String.format("%.1f",movieData.rating)}".also {
                movieItem.tvRating.text = it
            }

            movieData.releaseDate.also {
                movieItem.tvReleaseDate.text = it
            }

            movieItem.movieImage.loadImage(isOriginalImage = false,movieData.posterPath)
            movieItem.container.setOnClickListener {
                movieClickListener.onMovieClick(movieData)
            }
        }
    }

    interface OnMovieClickListener{
        fun onMovieClick(movieData : MovieEntity)
    }
}