package edu.utap.watchlist.api

class MediaItems(var tvList: List<TVShow>?, var movieList: List<Movie>?) {

    var mediaList =  listOf<MediaItem>()

    init {
        if(tvList != null) {
            mediaList = tvList!!.map {
                MediaItem(it.title, it.id, "TV", it.posterPath)
            }
        }
        else {
            mediaList = movieList!!.map {
                MediaItem(it.title, it.id, "Movie", it.posterPath)
            }
        }
    }


}