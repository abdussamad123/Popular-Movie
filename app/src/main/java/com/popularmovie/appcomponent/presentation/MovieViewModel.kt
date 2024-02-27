package com.popularmovie.appcomponent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popularmovie.appcomponent.data.local.entities.GenreEntity
import com.popularmovie.appcomponent.data.local.entities.MovieEntity
import com.popularmovie.appcomponent.domain.models.response.MovieDetailsResponse
import com.popularmovie.appcomponent.domain.repository.AppRepository
import com.popularmovie.appcomponent.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel() {

    private val _movieList = MutableStateFlow<Resource<out List<MovieEntity>>>(Resource.Loading())
    val fetchMovieList = _movieList.asStateFlow()

    fun fetchMovieList(sortBy : String,limit : Int, page : Int) {
        viewModelScope.launch {
            appRepository.getMovies(sortBy,limit,page*limit,page).collectLatest {
                _movieList.emit(it)
            }
        }
    }

    private val _genresList = MutableStateFlow<Resource<out List<GenreEntity>>>(Resource.Loading())
    val fetchGenresList = _genresList.asStateFlow()

    fun fetchGenresList() {
        viewModelScope.launch {
            appRepository.getGenresList() .collectLatest {
                _genresList.emit(it)
            }
        }
    }

    private val _getMovieDetailsApi = MutableStateFlow<Resource<out MovieDetailsResponse>>(Resource.Loading())
    val getMovieDetailsApi = _getMovieDetailsApi.asStateFlow()

    fun getMovieDetailsApi(movieId: Long) {
        viewModelScope.launch {
            appRepository.getMovieDetailsApi(movieId).collectLatest {
                _getMovieDetailsApi.emit(it)
            }
        }
    }


    fun fetchGenre(genreId : Int) = appRepository.getGenre(genreId)
    fun getMoviesBaseOnGenre(genreId: Int) = appRepository.getMoviesBaseOnGenre(genreId)
    fun getMovies(movieId: Long) = appRepository.getMovie(movieId)

}