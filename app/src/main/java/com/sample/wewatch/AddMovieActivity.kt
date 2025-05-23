package com.sample.wewatch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sample.wewatch.model.LocalDataSource
import com.sample.wewatch.model.Movie
import com.sample.wewatch.network.RetrofitClient.TMDB_IMAGEURL

import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class AddMovieActivity : AppCompatActivity() {
  private lateinit var titleEditText: EditText
  private lateinit var releaseDateEditText: EditText
  private lateinit var movieImageView: ImageView
  private lateinit var dataSource: LocalDataSource

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_movie)
    setupViews()
    dataSource = LocalDataSource(application)
  }

  fun setupViews() {
    titleEditText = findViewById(R.id.movie_title)
    releaseDateEditText = findViewById(R.id.movie_release_date)
    movieImageView = findViewById(R.id.movie_imageview)
  }

  //search onClick
  fun goToSearchMovieActivity(v: View) {
    val title = titleEditText.text.toString()
    val intent = Intent(this@AddMovieActivity, SearchActivity::class.java)
    intent.putExtra(SearchActivity.SEARCH_QUERY, title)
    startActivityForResult(intent, SEARCH_MOVIE_ACTIVITY_REQUEST_CODE)
  }

  //addMovie onClick
  fun onClickAddMovie(v: View) {
    if (TextUtils.isEmpty(titleEditText.text)) {
      showToast("Movie title cannot be empty")
      return
    }

    val title = titleEditText.text.toString()
    val releaseDate = releaseDateEditText.text.toString()
    val posterPath = movieImageView.tag?.toString() ?: ""

    val movie = Movie(title = title, releaseDate = releaseDate, posterPath = posterPath)

    // Запускаем корутину для выполнения в фоне
    CoroutineScope(Dispatchers.IO).launch {
      try {
        dataSource.insert(movie)
        // Возвращаемся в главный поток для UI операций
        withContext(Dispatchers.Main) {
          setResult(Activity.RESULT_OK)
          finish()
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          showToast("Error saving movie: ${e.message}")
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    this@AddMovieActivity.runOnUiThread {
      titleEditText.setText(data?.getStringExtra(SearchActivity.EXTRA_TITLE))
      releaseDateEditText.setText(data?.getStringExtra(SearchActivity.EXTRA_RELEASE_DATE))
      movieImageView.tag = data?.getStringExtra(SearchActivity.EXTRA_POSTER_PATH)
      Picasso.get().load(TMDB_IMAGEURL + data?.getStringExtra(SearchActivity.`EXTRA_POSTER_PATH`)).into(movieImageView)
    }
  }

  fun showToast(string: String) {
    Toast.makeText(this@AddMovieActivity, string, Toast.LENGTH_LONG).show()
  }

  companion object {
    const val SEARCH_MOVIE_ACTIVITY_REQUEST_CODE = 2
  }
}
