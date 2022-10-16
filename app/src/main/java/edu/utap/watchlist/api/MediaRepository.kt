package edu.utap.watchlist.api

class MediaRepository(private val api: MovieDBApi) {

    //POPULAR
    suspend fun fetchPopularMovies(): List<Movie> {
        return api.getMoviesPopular().results
    }

    suspend fun fetchPopularTV(): List<TVShow> {
        return api.getTVPopular().results
    }

    //LATEST
//    suspend fun fetchLatestMovies(): List<Movie> {
//        return api.getMoviesLatest().results
//    }
//
//    suspend fun fetchLatestTV(): List<TVShow> {
//        return api.getTVLatest().results
//    }

    //TOP RATED
    suspend fun fetchTopRatedMovies(): List<Movie> {
        return api.getMoviesTopRated().results
    }

    suspend fun fetchTopRatedTV(): List<TVShow> {
        return api.getTVTopRated().results
    }

    //NOW PLAYING
    suspend fun fetchPlayingMovies(): List<Movie> {
        return api.getMoviesNowPlaying().results
    }

    suspend fun fetchPlayingTV(): List<TVShow> {
        return api.getTVNowPlaying().results
    }

}