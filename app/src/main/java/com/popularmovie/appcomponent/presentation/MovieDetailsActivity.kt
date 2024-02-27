package com.popularmovie.appcomponent.presentation

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.popularmovie.R
import com.popularmovie.appcomponent.domain.models.response.MovieDetailsResponse
import com.popularmovie.appcomponent.util.Resource
import com.popularmovie.appcomponent.util.loadImage
import com.popularmovie.appcomponent.util.log
import com.popularmovie.appcomponent.util.toastShort
import com.popularmovie.databinding.ActivityMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private val viewModel: MovieViewModel by  viewModels()
    private lateinit var binding : ActivityMovieDetailsBinding
    private var movieId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        intent.extras?.let {
            movieId = it.getLong("movie_id")
        }
        log("MovieId", movieId.toString())
        viewModel.getMovieDetailsApi(movieId)
        collectMovieDetails()

    }

    private fun collectMovieDetails(){
        lifecycleScope.launch {
            collectLatestLifecycleFlow(viewModel.getMovieDetailsApi){
               when(it){
                   is Resource.Loading -> {

                   }
                   is Resource.Error -> {
                       it.error?.message?.let {
                           toastShort(it)
                       }
                   }
                   is Resource.Success -> {
                       it.data?.let {movieDetailsData->
                           setUpUI(movieDetailsData)
                       }
                   }

               }
            }
        }
    }

    private fun setUpUI(movieDetailsResponse: MovieDetailsResponse) {
        binding.ivImage.loadImage(true,movieDetailsResponse.poster_path)
        binding.tvTitle.text = movieDetailsResponse.original_title
        binding.tvOverView.text = movieDetailsResponse.overview

        "Released On : ${movieDetailsResponse.release_date}".also {
            binding.tvReleaseDate.text = it
        }

        log("WhoCanWatch", movieDetailsResponse.adult.toString())
        if (movieDetailsResponse.adult){
            "Who can Watch : ${getString(R.string.only_for_adult)}".also {
                binding.tvWhoCanWatch.text = it
            }
        }else {
            "Who can Watch : ${getString(R.string.evryone_can_watch)}".also {
                binding.tvWhoCanWatch.text = it
            }
        }

        "Vote Avg : ${movieDetailsResponse.vote_average}".also {
            binding.tvVoteAvg.text = it
        }
        var genresName = ""
        movieDetailsResponse.genres.forEach {
            genresName+=it.name+","
        }

        binding.tvGenres.text = genresName

    }

    private suspend fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            flow.collectLatest(collect)
        }
    }
}