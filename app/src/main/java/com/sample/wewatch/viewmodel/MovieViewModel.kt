package com.sample.wewatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sample.wewatch.model.LocalDataSource
import com.sample.wewatch.model.Movie
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val dataSource = LocalDataSource(application)

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadMovies() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val moviesList = dataSource.allMovies
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .blockingFirst()
                _movies.value = moviesList
            } catch (e: Exception) {
                _error.value = "Error fetching movies: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteMovies(moviesToDelete: List<Movie>) {
        viewModelScope.launch {
            try {
                moviesToDelete.forEach { movie ->
                    dataSource.delete(movie)
                }
                loadMovies() // Обновляем список после удаления
            } catch (e: Exception) {
                _error.value = "Error deleting movies: ${e.message}"
            }
        }
    }
}