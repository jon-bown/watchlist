package edu.utap.watchlist.api

import edu.utap.watchlist.MainActivity
import edu.utap.watchlist.providers.RegionContainer
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDBApi {
    object apikey {
        const val KEY = "9bcd9c1a6267ef7c56f11da1295bf749"
        const val LANG = "en-US"
        const val ADULT = "false"
    }
    //////MOVIES/////////
    // Get Movie Details
    @GET("/3/movie/{movie}?api_key=${apikey.KEY}")
    suspend fun getMovieDetails(@Path("movie") movie: String, @Query("language") lang: String) : Movie

    // Movie Providers
    @GET("/3/movie/{movie}/watch/providers?api_key=${apikey.KEY}")
    suspend fun getMovieProviders(@Path("movie") movie: String) : ProviderResponse

    //Similar
    @GET("/3/movie/{movie}/similar?api_key=${apikey.KEY}")
    suspend fun getSimilarMovies(@Path("movie") movie: String, @Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse


    //Recommended
    @GET("/3/movie/{movie}/recommendations?api_key=${apikey.KEY}")
    suspend fun getRecommendedMovies(@Path("movie") movie: String, @Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse



    //Get Upcoming
    @GET("/3/movie/upcoming?api_key=${apikey.KEY}")
    suspend fun getMoviesUpcoming(@Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse

    //Get Now Playing
    @GET("/3/movie/now_playing?api_key=${apikey.KEY}")
    suspend fun getMoviesNowPlaying(@Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse

    //Get Top Rated
    @GET("/3/movie/top_rated?api_key=${apikey.KEY}")
    suspend fun getMoviesTopRated(@Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse

    //Get Latest
    @GET("/3/movie/latest?api_key=${apikey.KEY}")
    suspend fun getMoviesLatest(@Query("language") lang: String, @Query("include_adult") adult: String) : Movie

    //Get Popular
    @GET("/3/movie/popular?api_key=${apikey.KEY}")
    suspend fun getMoviesPopular(@Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse

    //Trending today
    @GET("/3/trending/movie/day?api_key=${apikey.KEY}")
    suspend fun getMoviesTrendingToday(@Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse

    //Trending this week
    @GET("/3/trending/movie/week?api_key=${apikey.KEY}")
    suspend fun getMoviesTrendingWeek(@Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse

    //////TV/////////

    // Get TV Details
    @GET("/3/tv/{tv}?api_key=${apikey.KEY}")
    suspend fun getTVDetails(@Path("tv") tv: String, @Query("language") lang: String,
                             @Query("include_adult") adult: String): TVShow


    //TV Providers
    @GET("/3/tv/{tv}/watch/providers?api_key=${apikey.KEY}")
    suspend fun getTVProviders(@Path("tv") tv: String) : ProviderResponse

    //Similar
    @GET("/3/tv/{tv}/similar?api_key=${apikey.KEY}")
    suspend fun getSimilarTV(@Path("tv") tv: String, @Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse


    //Recommended
    @GET("/3/tv/{tv}/recommendations?api_key=${apikey.KEY}")
    suspend fun getRecommendedTV(@Path("tv") tv: String, @Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse


    //Get TV Airing Today
    @GET("3/tv/airing_today?api_key=${apikey.KEY}")
    suspend fun getTVAiringToday(@Query("language") lang: String, @Query("include_adult") adult: String) : TVListResponse

    //Get Upcoming
    @GET("3/tv/upcoming?api_key=${apikey.KEY}")
    suspend fun getTVUpcoming(@Query("language") lang: String, @Query("include_adult") adult: String) : TVListResponse

    //Get On Air
    @GET("/3/tv/on_the_air?api_key=${apikey.KEY}")
    suspend fun getTVNowPlaying(@Query("language") lang: String, @Query("include_adult") adult: String) : TVListResponse

    //Get Top Rated
    @GET("/3/tv/top_rated?api_key=${apikey.KEY}")
    suspend fun getTVTopRated(@Query("language") lang: String, @Query("include_adult") adult: String) : TVListResponse

    //Get Latest
    @GET("/3/tv/latest?api_key=${apikey.KEY}")
    suspend fun getTVLatest(@Query("language") lang: String, @Query("include_adult") adult: String) : TVShow

    //Get Popular
    @GET("/3/tv/popular?api_key=${apikey.KEY}")
    suspend fun getTVPopular(@Query("language") lang: String, @Query("include_adult") adult: String) : TVListResponse

    //Trending today
    @GET("/3/trending/tv/day?api_key=${apikey.KEY}")
    suspend fun getTVTrendingToday(@Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse

    //Trending this week
    @GET("/3/trending/tv/week?api_key=${apikey.KEY}")
    suspend fun getTVTrendingWeek(@Query("language") lang: String, @Query("include_adult") adult: String) : MovieListResponse



    data class MovieResponse(val data: Movie)
    data class MovieListResponse(val results: List<Movie>)



    data class TVResponse(val data: TVShow)
    data class TVListResponse(val results: List<TVShow>)

    data class ProviderResponse(val results: List<RegionContainer>)


    //SEARCH
    @GET("/3/search/movie?api_key=${apikey.KEY}")
    suspend fun searchMovies(@Query("query") query: String, @Query("language") lang: String,
                             @Query("include_adult") adult: String) : MovieListResponse

    @GET("/3/search/tv?api_key=${apikey.KEY}")
    suspend fun searchTVShows(@Query("query") query: String, @Query("language") lang: String,
                              @Query("include_adult") adult: String) : TVListResponse

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