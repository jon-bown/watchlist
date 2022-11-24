package edu.utap.watchlist.api

data class User(
    var adult: Boolean? = false,
    var country: String? = "",
    var language: String? = "",
    var lists: Map<String, List<MediaItem>>? = emptyMap(),
    var seen: List<String> = emptyList()
)

