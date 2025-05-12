package com.sample.wewatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sample.wewatch.model.LocalDataSource
import com.sample.wewatch.model.Movie
import com.sample.wewatch.network.RetrofitClient
import com.sample.wewatch.network.RetrofitInterface
import kotlinx.coroutines.launch

class AddMovieViewModel(application: Application) : AndroidViewModel(application) {
    private val dataSource = LocalDataSource(application)

    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitClient.moviesApi.searchMovie(
                    RetrofitClient.API_KEY,
                    query
                ).blockingFirst()

                _searchResults.value = response.results?.map { result ->
                    Movie(
                        id = result.id,
                        title = result.title,
                        releaseDate = result.releaseDate,
                        posterPath = result.posterPath,
                        overview = result.overview,
                        voteAverage = result.voteAverage
                    )
                }
            } catch (e: Exception) {
                _error.value = "Error searching movies: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                dataSource.insert(movie)
            } catch (e: Exception) {
                _error.value = "Error adding movie: ${e.message}"
            }
        }
    }
}