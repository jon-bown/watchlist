package edu.utap.watchlist.providers

import com.google.gson.annotations.SerializedName

data class Provider(
    @SerializedName("logo_path")
    val logoURL: String,
    @SerializedName("provider_name")
    val name: String
)
