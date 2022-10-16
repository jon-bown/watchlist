package edu.utap.watchlist.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MovieDBApi {
    object apikey {
        const val KEY = "9bcd9c1a6267ef7c56f11da1295bf749"
    }
    //////MOVIES/////////
    // Get Movie Details
    @GET("/3/movie/{movie}?api_key=${apikey.KEY}")
    suspend fun getMovieDetails(@Path("movie") movie: String) : Movie

    //Get Upcoming
    @GET("3/movie/upcoming?api_key=${apikey.KEY}")
    suspend fun getMoviesUpcoming() : MovieListResponse

    //Get Now Playing
    @GET("/3/movie/now_playing?api_key=${apikey.KEY}")
    suspend fun getMoviesNowPlaying() : MovieListResponse

    //Get Top Rated
    @GET("/3/movie/top_rated?api_key=${apikey.KEY}")
    suspend fun getMoviesTopRated() : MovieListResponse

    //Get Latest
    @GET("/3/movie/latest?api_key=${apikey.KEY}")
    suspend fun getMoviesLatest() : Movie

    //Get Popular
    @GET("/3/movie/popular?api_key=${apikey.KEY}")
    suspend fun getMoviesPopular() : MovieListResponse

    //////TV/////////

    // Get TV Details
    @GET("/3/tv/{tv}?api_key=${apikey.KEY}")
    suspend fun getTVDetails(@Path("tv") tv: String): TVShow

    //Get TV Airing Today
    @GET("3/tv/airing_today?api_key=${apikey.KEY}")
    suspend fun getTVAiring() : TVListResponse

    //Get Upcoming
    @GET("3/tv/upcoming?api_key=${apikey.KEY}")
    suspend fun getTVUpcoming() : TVListResponse

    //Get On Air
    @GET("/3/tv/on_the_air?api_key=${apikey.KEY}")
    suspend fun getTVNowPlaying() : TVListResponse

    //Get Top Rated
    @GET("/3/tv/top_rated?api_key=${apikey.KEY}")
    suspend fun getTVTopRated() : TVListResponse

    //Get Latest
    @GET("/3/tv/latest?api_key=${apikey.KEY}")
    suspend fun getTVLatest() : TVShow

    //Get Popular
    @GET("/3/tv/popular?api_key=${apikey.KEY}")
    suspend fun getTVPopular() : TVListResponse


    data class MovieResponse(val data: Movie)
    data class MovieListResponse(val results: List<Movie>)



    data class TVResponse(val data: TVShow)
    data class TVListResponse(val results: List<TVShow>)




    companion object {

        // Leave this as a simple, base URL.  That way, we can have many different
        // functions (above) that access different "paths" on this server
        // https://square.github.io/okhttp/4.x/okhttp/okhttp3/-http-url/
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("api.themoviedb.org")
            .build()

        // Public create function that ties together building the base
        // URL and the private create function that initializes Retrofit
        fun create(): MovieDBApi = create(url)
        private fun create(httpUrl: HttpUrl): MovieDBApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MovieDBApi::class.java)
        }
    }

}