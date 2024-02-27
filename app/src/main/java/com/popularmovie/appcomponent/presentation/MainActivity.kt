package com.popularmovie.appcomponent.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popularmovie.R
import com.popularmovie.appcomponent.data.local.entities.GenreEntity
import com.popularmovie.appcomponent.data.local.entities.MovieEntity
import com.popularmovie.appcomponent.data.local.entities.MoviesGenreEntity
import com.popularmovie.appcomponent.util.ApiConstants
import com.popularmovie.appcomponent.util.Resource
import com.popularmovie.appcomponent.util.log
import com.popularmovie.appcomponent.util.toastShort
import com.popularmovie.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

   /* private val progress: AlertDialog? by lazy {
        AppUtil.getProgressDialog(this)
    }

    private fun showProgress() {
        progress?.show()
    }

    private fun hideProgress() {
        progress?.dismiss()
    }*/

    private val viewModel: MovieViewModel by  viewModels()
    private lateinit var binding : ActivityMainBinding
    private var genresList = ArrayList<SortFilterBottomSheet.SortAndFilterItem>()
    private lateinit var movieAdaptor : MovieAdaptor
    private var moviesListBasedOnGenre = ArrayList<MovieEntity>()
    private val pageSize = 20
    private var page = 0
    private var isLastPage = false
    private var isLoading = false
    private var selectedSortType = ApiConstants.MOVIE_TYPE_UPCOMING
    private lateinit var loaderProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        loaderProgressBar = binding.progressBar
        viewModel.fetchMovieList(ApiConstants.MOVIE_TYPE_UPCOMING,pageSize,page)

        collectGenresListResponse()
        collectMovieListResponse()
        setUpMovieRV()
        initScrollListener()

    }

    override fun onResume() {
        super.onResume()
        binding.tvSortBy.setOnClickListener {
            showSortAndFilterBottomSheet(getString(R.string.sort_by), getSortByData(),false)
        }

        binding.tvFilterBy.setOnClickListener {
            viewModel.fetchGenresList()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setUpMovieRV(){
       movieAdaptor =  MovieAdaptor(object : MovieAdaptor.OnMovieClickListener{
            override fun onMovieClick(movieData: MovieEntity) {
                navigate(movieId = movieData.movieId)
            }
        })
        binding.rvMovie.layoutManager = GridLayoutManager(this,3)
        binding.rvMovie.adapter = movieAdaptor
    }

    private fun showSortAndFilterBottomSheet(title: String, listItems: ArrayList<SortFilterBottomSheet.SortAndFilterItem>
                                             ,openingForFilter:Boolean){

      SortFilterBottomSheet.getInstance(title,listItems,openingForFilter,object : SortFilterBottomSheetCallbackListener{
          override fun onClickItem(sortAndFilterItem: SortFilterBottomSheet.SortAndFilterItem) {
              // call api here
              updateSortAndFilterUI(openingForFilter,sortAndFilterItem)

              if (openingForFilter){
                  // render data based on filter
                  movieAdaptor.clearData()
                  getMoviesBasedOnGenre(sortAndFilterItem.itemId)
              }else {
                  // by sort
                  when(sortAndFilterItem.itemId){
                      1->{
                          page = 0
                          movieAdaptor.clearData()
                          viewModel.fetchMovieList(ApiConstants.MOVIE_TYPE_UPCOMING,pageSize,page)
                          selectedSortType = ApiConstants.MOVIE_TYPE_UPCOMING
                      }
                      2->{
                          page = 0
                          movieAdaptor.clearData()
                          viewModel.fetchMovieList(ApiConstants.MOVIE_TYPE_TOP_RATED,pageSize,page)
                          selectedSortType = ApiConstants.MOVIE_TYPE_TOP_RATED

                      }
                      3->{
                          page = 0
                          movieAdaptor.clearData()
                          viewModel.fetchMovieList(ApiConstants.MOVIE_TYPE_NOW_PLAYING,pageSize,page)
                          selectedSortType = ApiConstants.MOVIE_TYPE_NOW_PLAYING
                      }
                      4->{
                          page = 0
                          movieAdaptor.clearData()
                          viewModel.fetchMovieList(ApiConstants.MOVIE_TYPE_POPULAR,pageSize,page)
                          selectedSortType = ApiConstants.MOVIE_TYPE_POPULAR
                      }
                  }

              }
          }
      }).show(supportFragmentManager, SortFilterBottomSheet.TAG)
    }

    private fun updateSortAndFilterUI(
        openingForFilter: Boolean,
        sortAndFilterItem: SortFilterBottomSheet.SortAndFilterItem,){
        if (openingForFilter){
            binding.tvSortBy.text = getString(R.string.sort_by)
            "${getString(R.string.filter_by)} ${sortAndFilterItem.itemName}".also {
                binding.tvFilterBy.text = it
            }
        }else {
            binding.tvFilterBy.text = getString(R.string.filter_by)
            "${getString(R.string.sort_by)} ${sortAndFilterItem.itemName}".also {
                binding.tvSortBy.text = it
            }
        }
    }
    private fun getSortByData(): ArrayList<SortFilterBottomSheet.SortAndFilterItem> {
        val listItems = ArrayList<SortFilterBottomSheet.SortAndFilterItem>()
        listItems.add(SortFilterBottomSheet.SortAndFilterItem(ApiConstants.UPCOMING,1))
        listItems.add(SortFilterBottomSheet.SortAndFilterItem(ApiConstants.TOP_RATED,2))
        listItems.add(SortFilterBottomSheet.SortAndFilterItem(ApiConstants.NOW_PLAYING,3))
        listItems.add(SortFilterBottomSheet.SortAndFilterItem(ApiConstants.POPULAR,4))
        return listItems
    }

    private fun collectGenresListResponse(){
        lifecycleScope.launch {
            collectLatestLifecycleFlow(viewModel.fetchGenresList){
                when(it){
                    is Resource.Loading -> {
                        showProgress()
                    }
                    is Resource.Error -> {
                        hideProgress()
                        it.error?.message?.let {errorMsg->
                            toastShort(errorMsg)
                        }
                    }
                    is Resource.Success -> {
                        hideProgress()
                        it.data?.let {
                            genresList.clear()
                            setGenresList(it)
                        }
                    }
                }
            }
        }
    }

    private fun collectMovieListResponse(){
        lifecycleScope.launch {
            collectLatestLifecycleFlow(viewModel.fetchMovieList){
                when(it){
                    is Resource.Loading -> {
                        showProgress()
                    }
                    is Resource.Error -> {
                        hideProgress()
                        it.error?.message?.let {errorMsg->

                        }
                    }
                    is Resource.Success -> {
                        hideProgress()
                        it.data?.let {
                            // set data on RV
                            if (it.isNotEmpty()) {
                                movieAdaptor.setData(it)
                                page++
                                log("PagePage", page.toString())
                            } else {
                                //isLastPage = true
                                //toastShort(getString(R.string.no_more_data))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setGenresList(genreEntityList: List<GenreEntity>) {
       genreEntityList.forEach {
           genresList.add(SortFilterBottomSheet.SortAndFilterItem(it.name,it.genreId))
       }
        showSortAndFilterBottomSheet(getString(R.string.filter_by), genresList,true)
    }


    private suspend fun <T> collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            flow.collectLatest(collect)
        }
    }

    private fun navigate(movieId: Long){
        val intent = Intent(this,MovieDetailsActivity::class.java)
        intent.putExtra("movie_id", movieId)
        startActivity(intent)
    }

    private fun getMoviesBasedOnGenre(genreId:Int){
        moviesListBasedOnGenre.clear()
       val listOfMoviesId:List<MoviesGenreEntity> = viewModel.getMoviesBaseOnGenre(genreId)
       val uniqueMovieList =  listOfMoviesId.flatMap { listOf(it.movieId) }.distinct()

        for (i in uniqueMovieList){
            moviesListBasedOnGenre.add(viewModel.getMovies(i))
        }

        movieAdaptor.setData(moviesListBasedOnGenre)
    }

    private fun initScrollListener() {
        binding.rvMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= pageSize
                    ) {
                        viewModel.fetchMovieList(selectedSortType,pageSize,page)
                    }
                }
            }
        })
    }

    private fun showProgress(){
        isLoading = true
        loaderProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        isLoading = false
        loaderProgressBar.visibility = View.GONE
    }
}