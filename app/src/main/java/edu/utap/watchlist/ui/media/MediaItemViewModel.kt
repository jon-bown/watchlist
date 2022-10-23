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



    private val languageSetting = MutableLiveData("")
    private val countrySetting = MutableLiveData("")








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