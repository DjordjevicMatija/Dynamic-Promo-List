package rs.ac.bg.etf.dm200157d.dynamicpromolist.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rs.ac.bg.etf.dm200157d.BuildConfig
import rs.ac.bg.etf.dm200157d.R
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.MovieApiService
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.MovieDataSource
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.MovieDataSourceImpl
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.VideoInfoApiService
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.VideoInfoDataSource
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.VideoInfoDataSourceImpl
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repository.Repository
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.repository.RepositoryImpl
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.UseCase
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.UseCaseImpl
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.VideoInfoUseCase
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.VideoInfoUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMovieApiService(@ApplicationContext context: Context): MovieApiService {
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

        return retrofit.create(MovieApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVideoInfoApiService(@ApplicationContext context: Context): VideoInfoApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.video_info_url))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(VideoInfoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieDataSource(
        movieService: MovieApiService
    ): MovieDataSource {
        return MovieDataSourceImpl(movieService)
    }

    @Provides
    @Singleton
    fun provideVideoInfoDataSource(
        videoInfoService: VideoInfoApiService
    ): VideoInfoDataSource {
        return VideoInfoDataSourceImpl(videoInfoService)
    }

    @Provides
    @Singleton
    fun provideRepository(
        movieDataSource: MovieDataSource,
        videoInfoDataSource: VideoInfoDataSource
    ): Repository {
        return RepositoryImpl(movieDataSource, videoInfoDataSource)
    }

    @Provides
    @Singleton
    fun provideUseCase(
        repository: Repository,
        @ApplicationContext context: Context
    ): UseCase {
        return UseCaseImpl(repository, context)
    }

    @Provides
    @Singleton
    fun provideVideoInfoUseCase(
        repository: Repository
    ): VideoInfoUseCase {
        return VideoInfoUseCaseImpl(repository)
    }
}