package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repositories

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rs.ac.bg.etf.dm200157d.BuildConfig
import rs.ac.bg.etf.dm200157d.R
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.models.MovieResponse

class MovieDataSourceImpl: MovieDataSource {
    private lateinit var movieService: MovieApiService

    fun initialize(context: Context) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.TMDB_API_KEY}")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.base_url))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        movieService = retrofit.create(MovieApiService::class.java)
    }

    override suspend fun getMovies(): List<MovieResponse> {
        TODO("Not yet implemented")
    }


}