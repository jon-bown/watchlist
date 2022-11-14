package edu.utap.watchlist.api

data class MediaItem(
    var title: String? = "",
    var id: Int? = 0,
    var type: String? = "",
    var imageURL: String? = ""
                     ) {


    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MediaItem

        if (this.id == other.id) return true

        return false
    }


}


