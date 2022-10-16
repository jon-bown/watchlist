package edu.utap.watchlist.api

class MediaRepository(private val api: MovieDBApi) {

    suspend fun fetchPopularMovies(): List<Movie> {
        return api.getMoviesPopular().results
    }



}