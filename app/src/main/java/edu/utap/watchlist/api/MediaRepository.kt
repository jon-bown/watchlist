package edu.utap.watchlist.api

class MediaRepository(private val api: MovieDBApi) {

    suspend fun fetchPopularMovies(): List<Movie> {
        return api.getMoviesPopular().results
    }

    suspend fun fetchPopularTV(): List<TVShow> {
        return api.getTVPopular().results
    }



}