package com.sample.wewatch.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocalDataSource(application: Application) {
  private val movieDao: MovieDao = LocalDatabase.getInstance(application).movieDao()

  fun getAllMovies(): LiveData<List<Movie>> {
    val liveData = MutableLiveData<List<Movie>>()
    CoroutineScope(Dispatchers.IO).launch {
      // Для Flow:
      movieDao.getMoviesFlow().collect { movies ->
        liveData.postValue(movies)
      }

      // Или если используем suspend функцию:
      // val movies = movieDao.getAllMoviesSuspend()
      // liveData.postValue(movies)
    }
    return liveData
  }

  suspend fun insert(movie: Movie) {
    movieDao.insert(movie)
  }

  suspend fun delete(movie: Movie) {
    movieDao.delete(movie.id)
  }

  suspend fun update(movie: Movie) {
    movieDao.update(movie)
  }

}