package edu.utap.watchlist.api


//List of MediaItem all in one single category
class MediaItems(var tvList: List<TVShow>?, var movieList: List<Movie>?) {

    var mediaList =  listOf<MediaItem>()

    init {
        if(tvList != null) {
            mediaList = tvList!!.map {
                MediaItem(it.title, it.id, it.posterPath)
            }
        }
        else {
            mediaList = movieList!!.map {
                MediaItem(it.title, it.id, it.posterPath)
            }
        }
    }


}