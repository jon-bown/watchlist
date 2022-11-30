package edu.utap.watchlist.api

import com.google.gson.annotations.SerializedName

data class Movie (
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("budget")
    val budget: Long,
    @SerializedName("id")
    val id: Int,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("popularity")
    val popularity: Float,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("runtime")
    val runtime: Int,
    @SerializedName("vote_average")
    val voteAverage: Float,
    @SerializedName("vote_count")
    val voteCount: Long,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("tagline")
    val tagline: String?,
    @SerializedName("revenue")
    val revenue: Long,
    @SerializedName("original_language")
    val originalLanguage: String,
)