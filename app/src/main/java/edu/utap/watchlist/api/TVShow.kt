package edu.utap.watchlist.api

import com.google.gson.annotations.SerializedName

data class TVShow (
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("first_air_date")
    val firstAirDate: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val title: String,
    @SerializedName("last_air_date")
    val lastAirDate: String,
    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int,
    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("tagline")
    val tagline: String?,
    @SerializedName("vote_average")
    val voteAverage: Float,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String,
                )
