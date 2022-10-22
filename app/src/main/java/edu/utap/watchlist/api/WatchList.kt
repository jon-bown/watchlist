package edu.utap.watchlist.api

//Named list of MediaItems
data class WatchList(
    val name: String,
    val items: MutableList<MediaItem>? = null
)
