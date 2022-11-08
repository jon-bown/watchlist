package edu.utap.watchlist.api

data class MediaItem(
    val title: String,
    val id: Int,
    val type: String,
    val imageURL: String?
                     ) {


    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MediaItem

        if (this.id == other.id) return true

        return false
    }


}


