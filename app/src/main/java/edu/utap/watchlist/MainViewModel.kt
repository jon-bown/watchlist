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
    fun observeCountrySetting(): LiveData<String> {
        return countrySetting
    }
    fun changeCountrySetting(newValue: String) {
        countrySetting.value = newValue
        userDB.setCountry(newValue)
    }

    private var languageSetting = MutableLiveData("")

    fun observeLanguageSetting(): LiveData<String> {
        return languageSetting
    }
    fun changeLanguageSetting(newValue: String) {
        languageSetting.value = newValue
        userDB.setLanguage(newValue)
    }



    private val movieApi = MovieDBApi.create()
    private val repository = MediaRepository(movieApi)
    private val mediaItems = MutableLiveData<List<MediaItem>>()
    //User Lists

    //Current List
    private val currentWatchList = MutableLiveData<WatchList>()
    fun setCurrentWatchList(name: String){
        //if name in watchlists throw a toast
        val lists = watchLists.value!!.filter{
            //name is unique
            it.name == name
        }
        currentWatchList.value = lists.first()
    }
    fun observeCurrentWatchList(): LiveData<WatchList>{
        return currentWatchList
    }

    //User watchlists
    private val watchLists = MutableLiveData<List<WatchList>>()
    fun observeWatchLists(): LiveData<List<WatchList>> {
        return watchLists
    }
    fun addNewWatchList(name: String){
        var currentLists = mutableListOf<WatchList>()
        if(watchLists.value != null) {
            currentLists.addAll(watchLists.value!!)
        }
        //error if list already exists
        currentLists.add(WatchList(name, mutableListOf<MediaItem>()))
        watchLists.postValue(currentLists)
    }

    fun addToWatchList(name: String) {
        //possibly need some error handling here
        val listWithName = watchLists.value!!.filter { it.name == name }.first()
        val newList = mutableListOf<MediaItem>()
        if(listWithName.items != null){
            newList.addAll(listWithName.items!!.toList())
        }

        newList.add(currentMediaItem.value!!)

        val currentWatchLists = watchLists.value
        val mutableWatchLists = currentWatchLists!!.toMutableList()
        mutableWatchLists.remove(listWithName)
        mutableWatchLists.add(WatchList(name, newList))
        watchLists.postValue(mutableWatchLists)
    }

    fun getWatchlistNamesThatContain(): List<String> {
        val id = currentMediaItem.value!!.id
        if(this.watchLists.value != null){
            val names = this.watchLists.value!!.filter { wlist ->
                id in wlist.items!!.map{it.id}
            }
                .map{ wlist ->
                wlist.name }
                return names
            }
        else{
            return emptyList()
        }
    }

    //Firebase
    private var userDB = UserDBClient()




    //Specific media item
    private val currentMediaItem = MutableLiveData<MediaItem>()
    fun observeCurrentMediaItem(): LiveData<MediaItem> {
        return currentMediaItem
    }

    private val currentMovie = MutableLiveData<Movie>()
    fun observeCurrentMovie(): LiveData<Movie> {
        return currentMovie
    }
    private val currentTV = MutableLiveData<TVShow>()
    fun observeCurrentTV(): LiveData<TVShow> {
        return currentTV
    }

    fun setUpCurrentMediaData(item: MediaItem) {
        // This is where the network request is initiated.
        currentMediaItem.value = item
        if(item.type == "MOVIE"){
            fetchCurrentMovie()
        }
        else {
            fetchCurrentTV()
        }
        fetchSimilar(1)
    }

    fun fetchCurrentMovie() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            currentMovie.postValue(repository.fetchMovieDetails(currentMediaItem.value!!.id.toString(),
                "${languageSetting.value}-${countrySetting.value}"))
            fetchDone.postValue(true)
        }
    }

    fun fetchCurrentTV() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            currentTV.postValue(repository.fetchTVDetails(currentMediaItem.value!!.id.toString(),
                "${languageSetting.value}-${countrySetting.value}"))
            fetchDone.postValue(true)
        }
    }


    //Similar
    private val similarMediaItems = MutableLiveData<List<MediaItem>>()
    fun observeSimilarMediaItems(): LiveData<List<MediaItem>> {
        return similarMediaItems
    }
    fun fetchSimilar(page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            if(currentMediaItem.value!!.type == "MOVIE"){
                val movieList = repository.fetchSimilarMovies(currentMediaItem.value!!.id.toString(),
                    "${languageSetting.value}-${countrySetting.value}", adultMode.value!!, page)
                similarMediaItems.postValue(MediaItems(tvList = null, movieList = movieList).mediaList)
                fetchDone.postValue(true)
            }
            else {
                val tvList = repository.fetchSimilarTV(currentMediaItem.value!!.id.toString(),
                    "${languageSetting.value}-${countrySetting.value}", adultMode.value!!, page)
                similarMediaItems.postValue(MediaItems(tvList = tvList, movieList = null).mediaList)
                fetchDone.postValue(true)
            }

        }
    }




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
        //page = 1
        fetchPopular(1)
        fetchNowPlaying(1)
        fetchTopRated(1)
    }

    //POPULAR
    fun fetchPopular(page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue

            if(movieMode.get()){
                var list = repository.fetchPopularMovies("${languageSetting}-${countrySetting}", adultMode.value!!, page)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    if(page == 1){
                        popularMediaItems.postValue(MediaItems(tvList = null, movieList = list).mediaList)
                    }
                    else {
                        val popList = (popularMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list).mediaList)
                        popularMediaItems.postValue(popList)
                    }

                }
            }
            else {
                var list = repository.fetchPopularTV("${languageSetting}-${countrySetting}", adultMode.value!!, page)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    popularMediaItems.postValue(MediaItems(list, movieList = null).mediaList)

                }
            }
        }
    }

    //LATEST
    fun fetchNowPlaying(page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            if(movieMode.get()){
                var list = repository.fetchPlayingMovies("${languageSetting}-${countrySetting}", adultMode.value!!, page)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    nowPlayingMediaItems.postValue(MediaItems(tvList = null, movieList = list).mediaList)
                }
            }
            else {
                var list = repository.fetchPlayingTV("${languageSetting}-${countrySetting}", adultMode.value!!, page)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    nowPlayingMediaItems.postValue(MediaItems(tvList = list, movieList = null).mediaList)
                }
            }

        }
    }


    //TOP RATED
    fun fetchTopRated(page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            if(movieMode.get()){
                var list = repository.fetchTopRatedMovies("${languageSetting}-${countrySetting}", adultMode.value!!, page)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    topRatedMediaItems.postValue(MediaItems(tvList = null, movieList = list).mediaList)
                }
            }
            else {
                var list = repository.fetchTopRatedTV("${languageSetting}-${countrySetting}", adultMode.value!!, page)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    topRatedMediaItems.postValue(MediaItems(tvList = list, movieList = null).mediaList)
                }
            }

        }
    }




    fun fetchSearchResults(query: String, page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            if(movieMode.get()){
                var list = repository.fetchSearchMovies(query, "${languageSetting}-${countrySetting}", adultMode.value!!, page)
                if(list.isNotEmpty()){
                    fetchDone.postValue(true)
                    mediaItems.postValue(MediaItems(tvList = null, movieList = list).mediaList)
                }
            }
            else {
                var list = repository.fetchSearchTV(query, "${languageSetting}-${countrySetting}", adultMode.value!!, page)
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
            fetchPopular(1)
            //mediaItems.notifyAll()
        }
        else if(type == "nowPlaying"){
            fetchNowPlaying(1)
        }
        else if(type == "topRated"){
            fetchTopRated(1)
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

    fun netFetchBackdropImage(imageView: ImageView, imagePath: String) {
        Glide.fetchBackdrop(safePiscumURL(imagePath), randomPiscumURL(imagePath), imageView)
    }



    ///////USER DATA//////////

    fun populateUserData() {

        val TAG = "VIEWMODEL"
        //languageSetting.postValue(userDB.getLanguage())
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            email.postValue(userDB.getEmail())
            displayName.postValue(userDB.getUserName())
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