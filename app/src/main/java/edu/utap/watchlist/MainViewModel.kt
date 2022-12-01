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
import edu.utap.watchlist.R
import edu.utap.watchlist.api.*
import edu.utap.watchlist.firestore.UserDBClient
import edu.utap.watchlist.glide.Glide
import edu.utap.watchlist.providers.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MainViewModel : ViewModel() {


    //Firebase
    private var userDB = UserDBClient()

    private val movieApi = MovieDBApi.create()
    private val repository = MediaRepository(movieApi)
    private val mediaItems = MutableLiveData<List<MediaItem>>()

    private var displayName = MutableLiveData("")
    private var email = MutableLiveData("")
    private var uid = MutableLiveData("")
    private var movieMode = MutableLiveData(true)
    private var searchMovieMode = MutableLiveData(true)

    fun observeSearchMovieMode(): LiveData<Boolean> {
        return searchMovieMode
    }

    fun updateSearchMovieMode(value: Boolean){
        searchMovieMode.value = value
    }

    private fun getSearchMovieMode(): Boolean {
        return searchMovieMode.value!!
    }

    fun observeMovieMode(): LiveData<Boolean> {
        return movieMode
    }

    private fun getMovieMode(): Boolean {
        return movieMode.value!!
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

    ////////////SEEN MEDIA ITEMS//////////////////
    private var seenMediaItems = MutableLiveData<List<String>>()
    fun observeSeenMediaItems(): LiveData<List<String>> {
        return seenMediaItems
    }

    fun fetchSeenMediaItems() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            seenMediaItems.postValue(userDB.getSeenMediaItems())
        }
    }

    fun addSeenMedia(item: String){
        if(!seenMediaItems.value!!.contains(item)){
            userDB.addSeenItem(item)
            fetchSeenMediaItems()
        }
    }
    fun removeSeenMedia(item: String) {
        userDB.removeSeenItem(item)
        fetchSeenMediaItems()
    }
    fun checkInSeenMediaItems(item: String): Boolean{
        return seenMediaItems.value!!.contains(item)
    }

    //User Lists

    //Current List
    private val currentWatchList = MutableLiveData<WatchList>()

    private val currentWatchListName = MutableLiveData("")
    fun observeCurrentWatchListName(): LiveData<String>{
        return currentWatchListName
    }
    fun setCurrentWatchList(name: String){
        //if name in watchlists throw a toast
        if(name != ""){
            val lists = watchLists.value!!.filter{
                //name is unique
                it.name == name
            }
            currentWatchList.value = lists.first()
            currentWatchListName.value = name
        }
        else {

            currentWatchListName.postValue("")
        }

    }
    fun setWatchListOnlySeen(){
        val lists = watchLists.value!!.filter{
            //name is unique
            it.name == currentWatchListName.value
        }
        val listF = lists.first().items!!.filter {
            seenMediaItems.value!!.contains(it.id.toString())
        }
        currentWatchList.postValue(WatchList(currentWatchListName.value, listF.toMutableList()))
    }

    fun setWatchListOnlyNotSeen() {
        val lists = watchLists.value!!.filter{
            //name is unique
            it.name == currentWatchListName.value
        }
        val listF = lists.first().items!!.filter {
            !seenMediaItems.value!!.contains(it.id.toString())
        }
        currentWatchList.postValue(WatchList(currentWatchListName.value, listF.toMutableList()))
    }

    fun restoreWatchList() {
        val lists = watchLists.value!!.filter{
            //name is unique
            it.name == currentWatchListName.value
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
    fun fetchWatchLists() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            val currentLists = userDB.getWatchLists()
            currentLists.sortedByDescending { it.name }
            if(currentWatchListName.value != ""){
                val listWithName = watchLists.value!!.filter { it.name == currentWatchList.value?.name }.first()
                Log.d("POSTING VALUE", listWithName.toString())
                currentWatchList.postValue(listWithName)
            }



            watchLists.postValue(currentLists)


        }
    }


    fun addNewWatchList(name: String){
        //error if list already exists
        val newList = WatchList(name, mutableListOf())
        userDB.addWatchList(newList)
        fetchWatchLists()
    }

    fun removeWatchList(name: String){

        val listWithName = watchLists.value!!.filter { it.name == name }.first()
        userDB.removeWatchList(listWithName)
        fetchWatchLists()

    }

    private fun postValueToWatchLists(list: List<WatchList>){
        this.watchLists.postValue(list)
    }

    fun addToWatchListLiveData(names: List<String>) : LiveData<List<WatchList>> {
        return userDB.refreshWatchLists(currentMediaItem.value!!, names, watchLists.value!!)
    }

    fun postWatchList(list: List<WatchList>) {
        watchLists.postValue(list)
    }

    fun addToWatchList(name: String){
        val listWithName = watchLists.value!!.filter { it.name == name }.first()
        userDB.addMediaItemToWatchlist(name, currentMediaItem.value!!, listWithName.items!!.size)
    }


    fun removeFromWatchList(name: String, mediaItem: MediaItem) {

        val listWithName = watchLists.value!!.filter { it.name == name }.first()
        val newList = mutableListOf<MediaItem>()
        if(listWithName.items != null){
            newList.addAll(listWithName.items!!.toList())
        }

        newList.remove(mediaItem)
        userDB.removeMediaItemFromWatchlist(listWithName.name!!, currentMediaItem.value!!)

    }

    fun removeFromWatchList(name: String) {

        userDB.removeMediaItemFromWatchlist(name, currentMediaItem.value!!)

    }


    fun removeFromCurrentWatchList(mediaItem: MediaItem) {

        val newList = currentWatchList.value!!
        newList.items!!.remove(mediaItem)
        userDB.removeMediaItemFromWatchlist(newList.name!!, mediaItem)
        currentWatchList.postValue(WatchList(newList.name, newList.items))
        //fetchWatchLists()
    }



    fun removeFromLists(addedLists: List<String>){
        for(list in watchLists.value!!){
            if(currentWatchListName.value != null){
                if(currentWatchListName.value!! == list.name!!){
                    removeFromCurrentWatchList(currentMediaItem.value!!)
                }
            }
            else if(list.name !in addedLists){
                removeFromWatchList(list.name!!, currentMediaItem.value!!)
            }
        }
        fetchWatchLists()
    }

    fun getWatchlistNamesThatContain(): List<String> {
        val id = currentMediaItem.value!!.id
        if(this.watchLists.value != null){
            val names = this.watchLists.value!!.filter { wlist ->
                id in wlist.items!!.map{it.id}
            }
                .map{ wlist ->
                wlist.name!! }
                return names
            }
        else{
            return emptyList()
        }
    }





    //Specific media item
    private val currentMediaItem = MutableLiveData<MediaItem>()
    fun observeCurrentMediaItem(): LiveData<MediaItem> {
        return currentMediaItem
    }


    private val currentMediaItems = MutableLiveData<Stack<MediaItem>>()
    fun observeCurrentMediaItemsStack(): LiveData<Stack<MediaItem>> {
        return currentMediaItems
    }

    /////////PROVIDERS//////////
    private val currentStreamingProviders = MutableLiveData<List<Provider>>()
    fun observeStreamingProviders(): LiveData<List<Provider>> {
        return currentStreamingProviders
    }

    private val currentBuyProviders = MutableLiveData<List<Provider>>()
    fun observeBuyProviders(): LiveData<List<Provider>> {
        return currentBuyProviders
    }

    private val currentRentProviders = MutableLiveData<List<Provider>>()
    fun observeRentProviders(): LiveData<List<Provider>> {
        return currentRentProviders
    }

    fun fetchProviders() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            if(currentMediaItem.value!!.type == "MOVIE"){

                val providerContainer = repository.fetchMovieProviders(currentMediaItem.value!!.id.toString(), countrySetting.value!!)
                if(providerContainer.flatRate != null){
                    currentStreamingProviders.postValue(providerContainer.flatRate!!)
                }
                else {
                    currentStreamingProviders.postValue(emptyList())
                }
                if(providerContainer.buy != null){
                    currentBuyProviders.postValue(providerContainer.buy!!)
                }
                else {
                    currentBuyProviders.postValue(emptyList())
                }
                if(providerContainer.rent != null){
                    currentRentProviders.postValue(providerContainer.buy!!)
                }
                else {
                    currentRentProviders.postValue(emptyList())
                }

                fetchDone.postValue(true)
            }
            else {

                val providerContainer = repository.fetchTVProviders(currentMediaItem.value!!.id.toString(), countrySetting.value!!)
                if(providerContainer.flatRate != null){
                    currentStreamingProviders.postValue(providerContainer.flatRate!!)
                }
                else {
                    currentStreamingProviders.postValue(emptyList())
                }
                if(providerContainer.buy != null){
                    currentBuyProviders.postValue(providerContainer.buy!!)
                }
                else {
                    currentBuyProviders.postValue(emptyList())
                }
                if(providerContainer.rent != null){
                    currentRentProviders.postValue(providerContainer.buy!!)
                }
                else {
                    currentRentProviders.postValue(emptyList())
                }

                fetchDone.postValue(true)
            }

        }
    }








    private val currentMovie = MutableLiveData<Movie?>()
    fun observeCurrentMovie(): LiveData<Movie?> {
        return currentMovie
    }

    private val currentTV = MutableLiveData<TVShow?>()
    fun observeCurrentTV(): LiveData<TVShow?> {
        return currentTV
    }



    fun popToLastMediaData(){

        val tempStack = currentMediaItems.value
        tempStack!!.pop()

        if(tempStack.isNotEmpty()){
            refreshCurrentMediaInfo(tempStack.peek())
        }
        else {
            currentMediaItem.value == null
        }
    }

    fun refreshCurrentMedia(){
        refreshCurrentMediaInfo(currentMediaItem.value!!)
    }

    private fun refreshCurrentMediaInfo(item: MediaItem){
        currentMediaItem.value = item
        if(item.type == "MOVIE"){
            fetchCurrentMovie()
            currentTV.value = null
        }
        else {
            fetchCurrentTV()
            currentMovie.value == null
        }
        fetchSimilar(1)
        fetchRecommended(1)
        fetchProviders()
        fetchSeenMediaItems()
    }


    fun setUpCurrentMediaData(item: MediaItem) {
        var tempStack = currentMediaItems.value
        if(tempStack == null || tempStack.isEmpty()){
            tempStack = Stack<MediaItem>()
        }
        tempStack!!.push(item)
        currentMediaItems.postValue(tempStack!!)
        refreshCurrentMediaInfo(item)
    }

    private fun fetchCurrentMovie() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            currentMovie.postValue(repository.fetchMovieDetails(currentMediaItem.value!!.id.toString(),
                "${languageSetting.value}-${countrySetting.value}"))
            fetchDone.postValue(true)
        }
    }

    private fun fetchCurrentTV() {
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

            val list: List<Any>
            if(currentMediaItem.value!!.type == "MOVIE"){
                list = repository.fetchSimilarMovies(currentMediaItem.value!!.id.toString(),
                    "${languageSetting.value}-${countrySetting.value}", adultMode.value!!, page, countrySetting.value!!)
            }
            else {
                list = repository.fetchSimilarTV(currentMediaItem.value!!.id.toString(),
                    "${languageSetting.value}-${countrySetting.value}", adultMode.value!!, page, countrySetting.value!!)
            }

            if(list.isNotEmpty()){
                fetchDone.postValue(true)
                if(page == 1){
                    similarMediaItems.postValue(emptyList())
                    if(currentMediaItem.value!!.type == "MOVIE"){
                        similarMediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        similarMediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(currentMediaItem.value!!.type == "MOVIE"){
                        val popList = (similarMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        similarMediaItems.postValue(popList)
                    }
                    else {
                        val popList = (similarMediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        similarMediaItems.postValue(popList)
                    }
                }

            }
            else {
                similarMediaItems.postValue(emptyList())
            }

        }
    }




    private val recommendedMediaItems = MutableLiveData<List<MediaItem>>()
    fun observeRecommendedMediaItems(): LiveData<List<MediaItem>> {
        return recommendedMediaItems
    }
    fun fetchRecommended(page: Int) {

        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {

            val list: List<Any>
            if(currentMediaItem.value!!.type == "MOVIE"){
                list = repository.fetchRecommendedMovies(currentMediaItem.value!!.id.toString(),
                    "${languageSetting.value}-${countrySetting.value}", adultMode.value!!, page, countrySetting.value!!)
            }
            else {
                list = repository.fetchRecommendedTV(currentMediaItem.value!!.id.toString(),
                    "${languageSetting.value}-${countrySetting.value}", adultMode.value!!, page, countrySetting.value!!)
            }

            if(list.isNotEmpty()){
                fetchDone.postValue(true)
                if(page == 1){
                    recommendedMediaItems.postValue(emptyList())
                    if(currentMediaItem.value!!.type == "MOVIE"){
                        recommendedMediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        recommendedMediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(currentMediaItem.value!!.type == "MOVIE"){
                        val popList = (recommendedMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        recommendedMediaItems.postValue(popList)
                    }
                    else {
                        val popList = (recommendedMediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        recommendedMediaItems.postValue(popList)
                    }
                }

            }
            else {
                recommendedMediaItems.postValue(emptyList())
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

    private val trendingTodayMediaItems = MutableLiveData<List<MediaItem>>()
    fun observeTrendingTodayMediaItems(): LiveData<List<MediaItem>> {
        return trendingTodayMediaItems
    }

    private val trendingWeekMediaItems = MutableLiveData<List<MediaItem>>()
    fun observeTrendingWeekMediaItems(): LiveData<List<MediaItem>> {
        return trendingWeekMediaItems
    }


    private val upcomingMediaItems = MutableLiveData<List<MediaItem>>()
    fun observeUpcomingMediaItems(): LiveData<List<MediaItem>> {
        return upcomingMediaItems
    }






    //Initial guess for width and height
    var width = 350
    var height = 500


    private var fetchDone : MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        //netRefresh()
    }

    fun netRefresh() {
        // This is where the network request is initiated.
        //page = 1
        fetchPopular(1)
        fetchNowPlaying(1)
        fetchTopRated(1)
        fetchTrendingToday(1)
        fetchTrendingWeek(1)
        fetchUpcoming(1)
    }

    //POPULAR
    fun fetchPopular(page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            // Update LiveData from IO dispatcher, use postValue
            var list: List<Any>
            if(getMovieMode()){
                list = repository.fetchPopularMovies("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
                list = list.sortedByDescending { it.popularity }
                fetchDone.postValue(true)
            }
            else {
                list = repository.fetchPopularTV("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
                list = list.sortedByDescending { it.popularity }
                fetchDone.postValue(true)
            }

            if(list.isNotEmpty()){
                if(page == 1){
                    if(getMovieMode()){
                        popularMediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        popularMediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(getMovieMode()){
                        val popList = (popularMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        popularMediaItems.postValue(popList)
                    }
                    else {
                        val popList = (popularMediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        popularMediaItems.postValue(popList)
                    }
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
            var list: List<Any>
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            if(getMovieMode()){
                list = repository.fetchPlayingMovies("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)

                list = list.sortedByDescending { sdf.parse(it.releaseDate) }
                fetchDone.postValue(true)

            }
            else {
                list = repository.fetchNowPlayingTV("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
                //list = list.sortedByDescending { sdf.parse(it.lastAirDate) }
                fetchDone.postValue(true)
            }
            if(list.isNotEmpty()){

                if(page == 1){
                    if(getMovieMode()){
                        nowPlayingMediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        nowPlayingMediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(getMovieMode()){
                        val mergedList = (nowPlayingMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        nowPlayingMediaItems.postValue(mergedList)
                    }
                    else {
                        val mergedList = (nowPlayingMediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        nowPlayingMediaItems.postValue(mergedList)
                    }
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
            var list: List<Any>
            if(getMovieMode()){
                list = repository.fetchTopRatedMovies("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            else {
                list = repository.fetchTopRatedTV("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            if(list.isNotEmpty()){
                fetchDone.postValue(true)
                if(page == 1){
                    if(getMovieMode()){
                        topRatedMediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        topRatedMediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(getMovieMode()){
                        val mergedList = (topRatedMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        topRatedMediaItems.postValue(mergedList)
                    }
                    else {
                        val mergedList = (topRatedMediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        topRatedMediaItems.postValue(mergedList)
                    }
                }

            }

        }
    }



    fun fetchTrendingToday(page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            var list: List<Any>
            if(getMovieMode()){
                list = repository.fetchMoviesTrendingToday("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            else {
                list = repository.fetchTVTrendingToday("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            fetchDone.postValue(true)
            if(list.isNotEmpty()){
                if(page == 1){
                    if(getMovieMode()){
                        trendingTodayMediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        trendingTodayMediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(getMovieMode()){
                        val mergedList = (trendingTodayMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        trendingTodayMediaItems.postValue(mergedList)
                    }
                    else {
                        val mergedList = (trendingTodayMediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        trendingTodayMediaItems.postValue(mergedList)
                    }
                }

            }

        }
    }

    fun fetchTrendingWeek(page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            var list: List<Any>
            if(getMovieMode()){
                list = repository.fetchMoviesTrendingWeek("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            else {
                list = repository.fetchTVTrendingWeek("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            if(list.isNotEmpty()){
                fetchDone.postValue(true)
                if(page == 1) {
                    if(getMovieMode()){
                        trendingWeekMediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        trendingWeekMediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(getMovieMode()){
                        val mergedList = (trendingWeekMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        trendingWeekMediaItems.postValue(mergedList)
                    }
                    else {
                        val mergedList = (trendingWeekMediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        trendingWeekMediaItems.postValue(mergedList)
                    }
                }

            }

        }
    }



    fun fetchUpcoming(page: Int) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {
            var list: List<Any>
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            if(getMovieMode()){
                list = repository.fetchUpcomingMovies("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
                list = list.filter { sdf.parse(it.releaseDate) >= Date()}
            }
            else {
                list = repository.fetchUpcomingTV("${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            if(list.isNotEmpty()){
                fetchDone.postValue(true)
                if(page == 1){
                    if(getMovieMode()){
                        upcomingMediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        upcomingMediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(getMovieMode()){
                        val mergedList = (upcomingMediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        upcomingMediaItems.postValue(mergedList)
                    }
                    else {
                        val mergedList = (upcomingMediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        upcomingMediaItems.postValue(mergedList)
                    }
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
            var list: List<Any>
            if(getSearchMovieMode()){
                list = repository.fetchSearchMovies(query, "${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            else {
                list = repository.fetchSearchTV(query, "${languageSetting}-${countrySetting}", adultMode.value!!, page, countrySetting.value!!)
            }
            if(list.isNotEmpty()){
                fetchDone.postValue(true)
                if(page == 1){
                    if(getSearchMovieMode()){
                        mediaItems.postValue(MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                    }
                    else {
                        mediaItems.postValue(MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                    }
                }
                else {
                    if(getSearchMovieMode()){
                        val mergedList = (mediaItems.value!!.toMutableList() + MediaItems(tvList = null, movieList = list as List<Movie>).mediaList)
                        mediaItems.postValue(mergedList)
                    }
                    else {
                        val mergedList = (mediaItems.value!!.toMutableList() + MediaItems(tvList = list as List<TVShow>, movieList = null).mediaList)
                        mediaItems.postValue(mergedList)
                    }
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


    fun updateMovieMode(value: Boolean){
        movieMode.value = value
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
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            populateUserData()

        } else {
            //Pop up toast
        }
    }


    /////////SETTINGS////////
    fun changeAdultMode(newValue: Boolean){
        adultMode.value = newValue
        userDB.setAdultMode(newValue)
    }




    ////////IMAGES//////////

    private fun safePiscumURL(path: String?): String {
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
    private fun randomPiscumURL(): String {
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority("motivatevalmorgan.com")
            .appendPath("wp-content")
            .appendPath("uploads/2016/06/default-movie.jpg")
        val url = builder.build().toString()
        Log.d(javaClass.simpleName, "Built: $url")
        return url
    }

    fun netFetchImage(imageView: ImageView, imagePath: String?) {
        if(imagePath == null){

            imageView.setImageResource(R.drawable.large_movie_poster)
        }
        else {
            Glide.fetch(safePiscumURL(imagePath), randomPiscumURL(), imageView)
        }

    }

    fun netFetchBackdropImage(imageView: ImageView, imagePath: String?) {
        Log.d("IMAGE", "${imagePath}")
        if(imagePath != null){
            Glide.fetchBackdrop(safePiscumURL(imagePath), randomPiscumURL(), imageView)
        }

    }



    ///////USER DATA//////////

    fun populateUserData() {
        userDB.populateUser()

        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO
        ) {

            val user = userDB.getUserData()
            email.postValue(userDB.getEmail())
            displayName.postValue(userDB.getUserName())


            languageSetting.postValue(user.language)
            countrySetting.postValue(user.country)
            netRefresh()
            watchLists.postValue(userDB.getWatchLists())
            adultMode.postValue(user.adult)
            seenMediaItems.postValue(user.seen)

            fetchDone.postValue(true)
        }


    }


}