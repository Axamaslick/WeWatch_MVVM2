package com.sample.wewatch.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {
  @Query("SELECT * FROM movie_table")
  fun getAllMovies(): LiveData<List<Movie>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(movie: Movie)

  @Query("DELETE FROM movie_table WHERE id = :id")
  suspend fun delete(id: Int?)

  @Update
  suspend fun update(movie: Movie)
}