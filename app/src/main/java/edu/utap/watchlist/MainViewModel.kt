package edu.utap.firebaseauth

import android.app.Activity.RESULT_OK
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.utap.watchlist.api.*
import edu.utap.watchlist.firestore.UserDBClient
import edu.utap.watchlist.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class MainViewModel : ViewModel() {



    private var displayName = MutableLiveData("")
    private var email = MutableLiveData("")
    private var uid = MutableLiveData("")
    private var movieMode = AtomicBoolean(true)
    fun getMediaMode(): Boolean {
        return movieMode.get()
    }
    //settings
    private var adultMode = MutableLiveData(false)
    fun observeAdultMode(): LiveData<Boolean> {
        return adultMode
    }

    private var countrySetting = MutableLiveData("All")
    private var countrySet = ""
    fun getCountry(): String {
        return countrySet
    }
    fun observeCountrySetting(): LiveData<String> {
        return countrySetting
    }
    fun changeCountrySetting(newValue: String) {
        countrySetting.value = newValue
        countrySet = newValue
    }

    private var languageSetting = MutableLiveData("")
    private var languageSet = ""
    fun getLanguage(): String {
        return languageSet
    }
    fun observeLanguageSetting(): LiveData<String> {
        return languageSetting
    }
    fun changeLanguageSetting(newValue: String) {
        languageSetting.value = newValue
        languageSet = newValue
    }


    private val movieApi = MovieDBApi.create()
    private val repository = MediaRepository(movieApi)
    private val mediaItems = MutableLiveData<List<MediaItem>>()
    //User Lists


    //Firebase
    private var userDB = UserDBClient()



    //Media Items
    private val popularMediaItems = MutableLiveData<List<MediaItem>>()
    fun observePopularMediaItems(): LiveData<List<MediaItem>> {
        return popularMediaItems
    }
    private val nowPlayingMediaItems = MutableLiveData<List<MediaItem>>()
    fun observeNowPlayingMediaItems(): LiveData<List<MediaItem>> {
        return nowPlayingMediaItems
    }
    private val topRatedMediaItems = MutableLiveData<List<MediaItem>>()
    fun observeTopRatedMediaItems(): LiveData<List<MediaItem>> {

        return topRatedMediaItems
    }
    private val userLists = MutableLiveData<List<WatchList>>()
    fun observeUserLists(): LiveData<List<WatchList>> {
        return userLists
    }




    //Initial guess for width and height
    var width = 350
    var height = 500


    var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        netRefresh()
    }

    fun netRefresh() {
        // This is where the network request is initiated.
        fetchPopular()
        fetchNowPlaying()
        fetchTopRated()
    }

    //POPULAR
    fun fetchPopular() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue

            if(movieMode.get()){
                var list = repository.fetchPopularMovies("${languageSetting}-${countrySetting}", adultMode.value!!)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    popularMediaItems.postValue(MediaItems(tvList = null, movieList = list).mediaList)
                }
            }
            else {
                var list = repository.fetchPopularTV("${languageSetting}-${countrySetting}", adultMode.value!!)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    popularMediaItems.postValue(MediaItems(list, movieList = null).mediaList)

                }
            }
        }
    }

    //LATEST
    fun fetchNowPlaying() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            if(movieMode.get()){
                var list = repository.fetchPlayingMovies("${languageSetting}-${countrySetting}", adultMode.value!!)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    nowPlayingMediaItems.postValue(MediaItems(tvList = null, movieList = list).mediaList)
                }
            }
            else {
                var list = repository.fetchPlayingTV("${languageSetting}-${countrySetting}", adultMode.value!!)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    nowPlayingMediaItems.postValue(MediaItems(tvList = list, movieList = null).mediaList)
                }
            }

        }
    }


    //TOP RATED
    fun fetchTopRated() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            if(movieMode.get()){
                var list = repository.fetchTopRatedMovies("${languageSetting}-${countrySetting}", adultMode.value!!)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    topRatedMediaItems.postValue(MediaItems(tvList = null, movieList = list).mediaList)
                }
            }
            else {
                var list = repository.fetchTopRatedTV("${languageSetting}-${countrySetting}", adultMode.value!!)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    topRatedMediaItems.postValue(MediaItems(tvList = list, movieList = null).mediaList)
                }
            }

        }
    }




    fun fetchSearchResults(query: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            if(movieMode.get()){
                var list = repository.fetchSearchMovies(query, "${languageSetting}-${countrySetting}", adultMode.value!!)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    mediaItems.postValue(MediaItems(tvList = null, movieList = list).mediaList)
                }
            }
            else {
                var list = repository.fetchSearchTV(query, "${languageSetting}-${countrySetting}", adultMode.value!!)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    mediaItems.postValue(MediaItems(tvList = list, movieList = null).mediaList)
                }
            }

        }
    }

    fun clearMediaItems() {
        mediaItems.postValue(emptyList())
    }



    // Observations
    fun observeFetchDone(): LiveData<Boolean> {
        return fetchDone
    }


    fun observeMedia(): LiveData<List<MediaItem>> {
        return mediaItems
    }

    fun updateMediaMode(value: Boolean, type: String) {
        movieMode.set(value)
        if(type == "popular"){
            fetchPopular()
            //mediaItems.notifyAll()
        }
        else if(type == "nowPlaying"){
            fetchNowPlaying()
        }
        else if(type == "topRated"){
            fetchTopRated()
        }

    }

    fun updateMediaType(value: Boolean){
        movieMode.set(value)
    }


    private fun userLogout() {
        displayName.postValue("No user")
        email.postValue("No email, no active user")
        uid.postValue("No uid, no active user")
    }

    fun updateUser() {
        // XXX Write me. Update user data in view model

        populateUserData()

    }

    fun observeDisplayName() : LiveData<String> {
        return displayName
    }
    fun observeEmail() : LiveData<String> {
        return email
    }
    fun observeUid() : LiveData<String> {
        return uid
    }
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userLogout()
        //userDB.signOut
    }


    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Log.d("SIGN IN GOOD", "COULD SIGN IN")
            populateUserData()

            //
            // ...
        } else {
            Log.d("SIGN IN ERROR", "COULDNT SIGN IN")
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }


    /////////SETTINGS////////
    fun changeAdultMode(newValue: Boolean){
        adultMode.value = newValue
        userDB.setAdultMode(newValue)
        //push change to firebase
    }




    ////////IMAGES//////////

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
            .appendPath("w$width")
            .appendPath(path)
        val url = builder.build().toString()
        Log.d(javaClass.simpleName, "Built: $url")
        return url
    }

    fun netFetchImage(imageView: ImageView, imagePath: String) {
        Glide.fetch(safePiscumURL(imagePath), randomPiscumURL(imagePath), imageView)
    }


    ///////USER DATA//////////

    fun populateUserData() {

        val TAG = "VIEWMODEL"
        //languageSetting.postValue(userDB.getLanguage())
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            languageSetting.postValue(userDB.getLanguage())
            countrySetting.postValue(userDB.getCountry())
            adultMode.postValue(userDB.getAdultMode())
            fetchDone.postValue(true)
        }


//        displayName.postValue(userDB.displayName)
//        email.value = userDB.email
//        uid.value = userDB.uid
        //Lists

        Log.d("POPULATE_L", languageSetting.value!!)
        Log.d("POPULATE_C", countrySetting.value!!)
    }


}