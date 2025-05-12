package com.sample.wewatch.model

import android.database.Observable
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface MovieDao {
  // Вариант 1: Использование Flow
  @Query("SELECT * FROM movie_table")
  fun getMoviesFlow(): Flow<List<Movie>>

  // Вариант 2: Или suspend функция
  @Query("SELECT * FROM movie_table")
  suspend fun getAllMoviesSuspend(): List<Movie>

  @Insert(onConflict = REPLACE)
  suspend fun insert(movie: Movie)

  @Query("DELETE FROM movie_table WHERE id = :id")
  suspend fun delete(id: Int?)

  @Update
  suspend fun update(movie: Movie)
}