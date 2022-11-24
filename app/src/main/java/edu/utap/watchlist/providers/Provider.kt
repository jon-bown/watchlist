package edu.utap.watchlist.providers

import com.google.gson.annotations.SerializedName
//SINGLE MEDIA PROVIDER
data class Provider(
    @SerializedName("logo_path")
    val logoURL: String,
    @SerializedName("provider_name")
    val name: String
)
