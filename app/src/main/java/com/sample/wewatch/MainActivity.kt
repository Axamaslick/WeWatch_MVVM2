package com.sample.wewatch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.wewatch.databinding.ActivityMainBinding
import com.sample.wewatch.model.Movie
import com.sample.wewatch.viewmodel.MovieViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private val viewModel: MovieViewModel by viewModels()
  private var adapter: MainAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupViews()
    setupObservers()
    loadMovies()
  }

  private fun setupViews() {
    binding.moviesRecyclerview.layoutManager = LinearLayoutManager(this)
    supportActionBar?.title = "Movies to Watch"

    binding.fab.setOnClickListener {
      goToAddMovieActivity(it)
    }
  }

  private fun setupObservers() {
    viewModel.movies.observe(this) { movies ->
      displayMovies(movies)
    }

    viewModel.error.observe(this) { errorMessage ->
      Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
  }

  private fun loadMovies() {
    lifecycleScope.launch {
      viewModel.loadMovies()
    }
  }

  fun displayMovies(movieList: List<Movie>?) {
    if (movieList.isNullOrEmpty()) {
      binding.moviesRecyclerview.visibility = View.INVISIBLE
      binding.noMoviesLayout.visibility = View.VISIBLE
    } else {
      adapter = MainAdapter(movieList, this)
      binding.moviesRecyclerview.adapter = adapter
      binding.moviesRecyclerview.visibility = View.VISIBLE
      binding.noMoviesLayout.visibility = View.INVISIBLE
    }
  }

  fun goToAddMovieActivity(v: View) {
    val intent = Intent(this, AddMovieActivity::class.java)
    startActivityForResult(intent, ADD_MOVIE_ACTIVITY_REQUEST_CODE)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ADD_MOVIE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      loadMovies()
      Toast.makeText(this, "Movie successfully added.", Toast.LENGTH_LONG).show()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.deleteMenuItem) {
      adapter?.selectedMovies?.let { selectedMovies ->
        if (selectedMovies.isNotEmpty()) {
          lifecycleScope.launch {
            viewModel.deleteMovies(selectedMovies.toList())
          }
          val message = if (selectedMovies.size == 1) "Movie deleted" else "Movies deleted"
          Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
      }
    }
    return super.onOptionsItemSelected(item)
  }

  companion object {
    const val ADD_MOVIE_ACTIVITY_REQUEST_CODE = 1
  }
}