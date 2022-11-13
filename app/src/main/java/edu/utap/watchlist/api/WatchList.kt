package edu.utap.watchlist.api

//Named list of MediaItems
data class WatchList(
    var name: String? = "",
    var items: MutableList<MediaItem>? = mutableListOf<MediaItem>()
)
