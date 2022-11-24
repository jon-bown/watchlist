package edu.utap.watchlist.providers

import com.google.gson.annotations.SerializedName

//CONTAINS ALL MEDIA PROVIDERS FOR A REGION
data class RegionContainer(
    @SerializedName("link")
    val linkURL: String,
    @SerializedName("flatrate")
    val flatRate: List<Provider>?,
    @SerializedName("rent")
    val rent: List<Provider>?,
    @SerializedName("buy")
    val buy: List<Provider>?,
)
