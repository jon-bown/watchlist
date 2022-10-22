package edu.utap.watchlist.ui.media

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.watchlist.api.*
import edu.utap.watchlist.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//VIew Model for currently selected media item
class MediaItemViewModel: ViewModel() {
    private val movieApi = MovieDBApi.create()
    private val repository = MediaRepository(movieApi)
    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)
    private val currentMovie = MutableLiveData<Movie>()
    fun observeCurrentMovie(): LiveData<Movie> {
        return currentMovie
    }
    private val currentTV = MutableLiveData<TVShow>()
    fun observeCurrentTV(): LiveData<TVShow> {
        return currentTV
    }
    private val mediaType = MutableLiveData("")
    private val mediaID = MutableLiveData("")
    private val languageSetting = MutableLiveData("")
    private val countrySetting = MutableLiveData("")



    fun setUpData(type: String, id: String, language: String, country: String) {
        // This is where the network request is initiated.
        mediaType.value = type
        mediaID.value = id
        languageSetting.value = language
        countrySetting.value = country

        if(mediaType.value == "Movie"){
            fetchCurrentMovie()
        }
        else {
            fetchCurrentTV()
        }
    }

    fun fetchCurrentMovie() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
                currentMovie.postValue(repository.fetchMovieDetails(mediaID.value!!,
                    "${languageSetting}-${countrySetting}"))
                fetchDone.postValue(true)
        }
    }

    fun fetchCurrentTV() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            currentTV.postValue(repository.fetchTVDetails(mediaID.value!!,
                "${languageSetting}-${countrySetting}"))
            fetchDone.postValue(true)
        }
    }





    ////////IMAGES///////
    private fun safePiscumURL(path: String): String {
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority("image.tmdb.org")
            .appendPath("t")
            .appendPath("p")
            .appendPath("original")
            .appendPath(path)
        val url = builder.build().toString()
        Log.d(javaClass.simpleName, "Built: $url")
        return url
    }

    // Generates greater variety of images and we can control the randomness
    // But some numbers return error images, so have a backup.
    private fun randomPiscumURL(path: String): String {
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority("image.tmdb.org")
            .appendPath("t")
            .appendPath("p")
            .appendPath("original")
            .appendPath(path)
        val url = builder.build().toString()
        Log.d(javaClass.simpleName, "Built: $url")
        return url
    }

    fun netFetchImage(imageView: ImageView, imagePath: String) {
        Glide.fetch(safePiscumURL(imagePath), randomPiscumURL(imagePath), imageView)
    }

}