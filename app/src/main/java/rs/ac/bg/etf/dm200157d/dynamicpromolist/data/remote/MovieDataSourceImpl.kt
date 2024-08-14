package rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rs.ac.bg.etf.dm200157d.BuildConfig
import rs.ac.bg.etf.dm200157d.R
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.NetworkResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.util.NetworkHelper

import javax.inject.Inject
import javax.inject.Singleton

class MovieDataSourceImpl @Inject constructor(
    private val movieService: MovieApiService
) : MovieDataSource {

    override suspend fun getMovies(): NetworkResponse<MovieListResponse> {
        return NetworkHelper.safeApiCall { movieService.getMovies() }
    }

    override suspend fun getVideos(id: Int): NetworkResponse<VideoListResponse> {
        return NetworkHelper.safeApiCall { movieService.getVideos(id) }
    }

}