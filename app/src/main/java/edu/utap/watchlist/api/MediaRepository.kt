package edu.utap.watchlist.api

import edu.utap.watchlist.providers.Provider
import edu.utap.watchlist.providers.RegionContainer
import retrofit2.http.Query

class MediaRepository(private val api: MovieDBApi) {

    ////////MOVIES//////////
    suspend fun fetchMovieDetails(movie: String, language: String): Movie {
        return api.getMovieDetails(movie, language)
    }

    suspend fun fetchMovieProviders(movie: String, region: String): RegionContainer {
        val regionList = api.getMovieProviders(movie).results
        if(regionList[region] == null) {
            return regionList["US"]!!
        }
        return regionList[region]!!
    }

    suspend fun fetchSimilarMovies(movie: String, language: String, adult: Boolean): List<Movie> {
        return api.getSimilarMovies(movie, language, adult.toString()).results
    }

    suspend fun fetchRecommendedMovies(movie: String, language: String, adult: Boolean): List<Movie> {
        return api.getRecommendedMovies(movie, language, adult.toString()).results
    }

    suspend fun fetchUpcomingMovies(language: String, adult: Boolean): List<Movie> {
        return api.getMoviesUpcoming(language, adult.toString()).results
    }


    suspend fun fetchMoviesTrendingToday(language: String, adult: Boolean): List<Movie> {
        return api.getMoviesTrendingToday(language, adult.toString()).results
    }

    suspend fun fetchMoviesTrendingWeek(language: String, adult: Boolean): List<Movie> {
        return api.getMoviesTrendingWeek(language, adult.toString()).results
    }

    suspend fun fetchPopularMovies(language: String, adult: Boolean): List<Movie> {
        return api.getMoviesPopular(language, adult.toString()).results
    }


    suspend fun fetchTopRatedMovies(language: String, adult: Boolean): List<Movie> {
        return api.getMoviesTopRated(language, adult.toString()).results
    }


    suspend fun fetchPlayingMovies(language: String, adult: Boolean): List<Movie> {
        return api.getMoviesNowPlaying(language, adult.toString()).results
    }

    suspend fun fetchSearchMovies(query: String, language: String, adult: Boolean): List<Movie>{
        return api.searchMovies(query, language, adult.toString()).results
    }

    //////////TV//////////////
    suspend fun fetchTVDetails(tv: String, language: String): TVShow {
        return api.getTVDetails(tv, language)
    }

    suspend fun fetchTVProviders(tv: String, region: String): RegionContainer {
        val regionList = api.getTVProviders(tv).results
        if(regionList[region] == null) {
            return regionList["US"]!!
        }
        return regionList[region]!!
    }



    suspend fun fetchSimilarTV(tv: String, language: String, adult: Boolean): List<TVShow> {
        return api.getSimilarTV(tv, language, adult.toString()).results
    }

    suspend fun fetchRecommendedTV(tv: String, language: String, adult: Boolean): List<TVShow> {
        return api.getRecommendedTV(tv, language, adult.toString()).results
    }

    suspend fun fetchTVTrendingToday(language: String, adult: Boolean): List<TVShow> {
        return api.getTVTrendingToday(language, adult.toString()).results
    }

    suspend fun fetchTVTrendingWeek(language: String, adult: Boolean): List<TVShow> {
        return api.getTVTrendingWeek(language, adult.toString()).results
    }

    suspend fun fetchPopularTV(language: String, adult: Boolean): List<TVShow> {
        return api.getTVPopular(language, adult.toString()).results
    }


//    suspend fun fetchLatestMovies(): List<Movie> {
//        return api.getMoviesLatest().results
//    }
//
//    suspend fun fetchLatestTV(): List<TVShow> {
//        return api.getTVLatest().results
//    }


    suspend fun fetchTopRatedTV(language: String, adult: Boolean): List<TVShow> {
        return api.getTVTopRated(language, adult.toString()).results
    }

    suspend fun fetchPlayingTV(language: String, adult: Boolean): List<TVShow> {
        return api.getTVNowPlaying(language, adult.toString()).results
    }

    suspend fun fetchSearchTV(query: String, language: String, adult: Boolean): List<TVShow>{
        return api.searchTVShows(query, language, adult.toString()).results
    }

}