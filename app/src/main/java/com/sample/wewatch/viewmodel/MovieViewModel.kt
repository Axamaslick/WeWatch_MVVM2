package com.sample.wewatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.wewatch.model.LocalDataSource
import com.sample.wewatch.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val dataSource = LocalDataSource(application)
    private val disposables = CompositeDisposable()

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadMovies() {
        _loading.value = true
        val disposable = dataSource.allMovies
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { movies ->
                    _movies.value = movies
                    _loading.value = false
                },
                { e ->
                    _error.value = "Error fetching movies: ${e.message}"
                    _loading.value = false
                }
            )
        disposables.add(disposable)
    }

    fun deleteMovies(moviesToDelete: List<Movie>) {
        moviesToDelete.forEach { movie ->
            dataSource.delete(movie)
        }
        loadMovies() // Обновляем список после удаления
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}