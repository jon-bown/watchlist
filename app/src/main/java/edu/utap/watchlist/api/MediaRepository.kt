package edu.utap.watchlist.api

import edu.utap.watchlist.providers.RegionContainer

class MediaRepository(private val api: MovieDBApi) {

    ////////MOVIES//////////
    suspend fun fetchMovieDetails(movie: String, language: String): Movie {
        return api.getMovieDetails(movie, language)
    }

    suspend fun fetchMovieProviders(movie: String, region: String): RegionContainer {
        val regionList = api.getMovieProviders(movie).results
        if(regionList[region] == null) {
            if(regionList["US"] != null){
                return regionList["US"]!!
            }

        }
        else {
            return regionList[region]!!
        }
        return RegionContainer("", null, null, null)
    }

    suspend fun fetchSimilarMovies(movie: String, language: String, adult: Boolean, page: Int): List<Movie> {
        return api.getSimilarMovies(movie, language, adult.toString(), page).results
    }

    suspend fun fetchRecommendedMovies(movie: String, language: String, adult: Boolean, page: Int): List<Movie> {
        return api.getRecommendedMovies(movie, language, adult.toString(), page).results
    }

    suspend fun fetchUpcomingMovies(language: String, adult: Boolean, page: Int): List<Movie> {
        return api.getMoviesUpcoming(language, adult.toString(), page).results
    }


    suspend fun fetchMoviesTrendingToday(language: String, adult: Boolean, page: Int): List<Movie> {
        return api.getMoviesTrendingToday(language, adult.toString(), page).results
    }

    suspend fun fetchMoviesTrendingWeek(language: String, adult: Boolean, page: Int): List<Movie> {
        return api.getMoviesTrendingWeek(language, adult.toString(), page).results
    }

    suspend fun fetchPopularMovies(language: String, adult: Boolean, page: Int): List<Movie> {
        return api.getMoviesPopular(language, adult.toString(), page).results
    }


    suspend fun fetchTopRatedMovies(language: String, adult: Boolean, page: Int): List<Movie> {
        return api.getMoviesTopRated(language, adult.toString(), page).results
    }


    suspend fun fetchPlayingMovies(language: String, adult: Boolean, page: Int): List<Movie> {
        return api.getMoviesNowPlaying(language, adult.toString(), page).results
    }

    suspend fun fetchSearchMovies(query: String, language: String, adult: Boolean, page: Int): List<Movie>{
        return api.searchMovies(query, language, adult.toString(), page).results
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



    suspend fun fetchSimilarTV(tv: String, language: String, adult: Boolean, page: Int): List<TVShow> {
        return api.getSimilarTV(tv, language, adult.toString(), page).results
    }

    suspend fun fetchRecommendedTV(tv: String, language: String, adult: Boolean, page: Int): List<TVShow> {
        return api.getRecommendedTV(tv, language, adult.toString(), page).results
    }

    suspend fun fetchTVTrendingToday(language: String, adult: Boolean, page: Int): List<TVShow> {
        return api.getTVTrendingToday(language, adult.toString(), page).results
    }

    suspend fun fetchTVTrendingWeek(language: String, adult: Boolean, page: Int): List<TVShow> {
        return api.getTVTrendingWeek(language, adult.toString(), page).results
    }

    suspend fun fetchPopularTV(language: String, adult: Boolean, page: Int): List<TVShow> {
        return api.getTVPopular(language, adult.toString(), page).results
    }

    suspend fun fetchUpcomingTV(language: String, adult: Boolean, page: Int): List<TVShow> {
        return api.getTVAiringToday(language, adult.toString(), page).results
    }

    suspend fun fetchNowPlayingTV(language: String, adult: Boolean, page: Int): List<TVShow> {
        return api.getTVOnTheAir(language, adult.toString(), page).results
    }


    suspend fun fetchLatestMovie(language: String, adult: Boolean): Movie {
        return api.getMoviesLatest(language, adult.toString())
    }

    suspend fun fetchLatestTV(language: String, adult: Boolean): TVShow {
        return api.getTVLatest(language, adult.toString())
    }


    suspend fun fetchTVStreamingRegionProviders(region: String, id: String): RegionContainer {
        val results = api.getTVProviders(id).results
        if(region in results.keys){
            return results[region]!!
        }
        else{
            return results["US"]!!
        }
    }

    suspend fun fetchMovieStreamingRegionProviders(region: String, id: String): RegionContainer {
        val results = api.getTVProviders(id).results
        if(region in results.keys){
            return results[region]!!
        }
        else{
            return results["US"]!!
        }
    }


    suspend fun fetchTopRatedTV(language: String, adult: Boolean, page: Int): List<TVShow> {
        return api.getTVTopRated(language, adult.toString(), page).results
    }

    suspend fun fetchSearchTV(query: String, language: String, adult: Boolean, page: Int): List<TVShow>{
        return api.searchTVShows(query, language, adult.toString(), page).results
    }

}