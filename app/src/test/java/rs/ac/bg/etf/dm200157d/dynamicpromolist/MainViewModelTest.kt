package rs.ac.bg.etf.dm200157d.dynamicpromolist

import android.os.Looper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.VideoResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.DataResult
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.UseCase
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.VideoInfoUseCase
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieDTO
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Video
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.VideoInfo
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.util.toVideo
import rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.MainViewModel
import rs.ac.bg.etf.dm200157d.dynamicpromolist.util.getOrAwaitValue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MainViewModelTest {

    private val useCase = mockk<UseCase>(relaxed = true)
    private val videoInfoUseCase = mockk<VideoInfoUseCase>(relaxed = true)
    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(useCase, videoInfoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMovies returns success`() = runTest {
        val mockMovieResponse = MovieResponse(
            id = 1,
            title = "Mock Movie",
            backdropPath = "/mock_backdrop.jpg",
            posterPath = "/mock_poster.jpg",
            voteAverage = 8.5,
            overview = "This is a mock movie."
        )

        val mockMovieListResponse = MovieListResponse(
            results = arrayListOf(mockMovieResponse)
        )

        val mockMovieDTOList = mockMovieListResponse.results.map { movieResponse ->
            MovieDTO(
                id = movieResponse.id,
                title = movieResponse.title,
                backdropPath = movieResponse.backdropPath,
                posterPath = movieResponse.posterPath,
                voteAverage = movieResponse.voteAverage,
                overview = movieResponse.overview
            )
        }

        coEvery { useCase.getMovies() } returns DataResult.Success(mockMovieDTOList)

        viewModel.getMovies()

        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val movies = viewModel.moviesLiveData.getOrAwaitValue()
        assertEquals(1, movies.size)
        assertEquals("Mock Movie", movies[0].title)
        assertEquals(8.5, movies[0].voteAverage)

        coVerify { useCase.getMovies() }
    }

    @Test
    fun `getMovies returns failure`() = runTest {
        val mockError = Exception("Failed to fetch movies")

        coEvery { useCase.getMovies() } returns DataResult.Failure(mockError)

        viewModel.getMovies()

        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val error = viewModel.errorLiveData.getOrAwaitValue()
        assertEquals(mockError, error)

        coVerify { useCase.getMovies() }
    }

    @Test
    fun `getVideo returns success trailer found`() = runTest {
        val mockVideoResponse1 = VideoResponse(
            key = "videoKey",
            site = "YouTube",
            type = "Trailer"
        )
        val mockVideoResponse2 = VideoResponse(
            key = "videoTeaserKey",
            site = "YouTube",
            type = "Teaser"
        )

        val mockVideoListResponse = VideoListResponse(
            id = 1,
            results = arrayListOf(mockVideoResponse1, mockVideoResponse2)
        )

        coEvery { useCase.getVideo(1) } returns DataResult.Success(mockVideoResponse1.toVideo())

        viewModel.getVideo(1) {}

        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val video = viewModel.videoLiveData.getOrAwaitValue()
        assertEquals("videoKey", video.key)

        coVerify { useCase.getVideo(1) }
    }

    @Test
    fun `getVideo returns success trailer not found`() = runTest {
        val mockVideoResponse = VideoResponse(
            key = "videoTeaserKey",
            site = "YouTube",
            type = "Teaser"
        )

        val mockVideoListResponse = VideoListResponse(
            id = 1,
            results = arrayListOf(mockVideoResponse)
        )

        coEvery { useCase.getVideo(1) } returns DataResult.Success(Video(null, null, null))

        viewModel.getVideo(1) {}

        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val video = viewModel.videoLiveData.getOrAwaitValue()
        assertNull(video.key)
        assertNull(video.site)
        assertNull(video.type)

        coVerify { useCase.getVideo(1) }
    }

    @Test
    fun `getVideo returns failure`() = runTest {
        val mockError = Exception("Failed to fetch videos")

        coEvery { useCase.getVideo(1) } returns DataResult.Failure(mockError)

        viewModel.getVideo(1) {}

        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val error = viewModel.errorLiveData.getOrAwaitValue()
        assertEquals(mockError, error)

        coVerify { useCase.getVideo(1) }
    }

    @Test
    fun `getVideoInfo returns success`() = runTest {
        val mockVideoResponse = VideoResponse(key = "videoKey", site = "YouTube", type = "Trailer")

        val mockVideoInfo = VideoInfo(audioUrl = "audioUrl", videoUrl = "videoUrl")
        coEvery { useCase.getVideo(1) } returns DataResult.Success(mockVideoResponse.toVideo())
        coEvery { videoInfoUseCase.getVideoInfo("videoKey") } returns DataResult.Success(mockVideoInfo)

        var onSuccessCalled = false
        viewModel.getVideo(1) {
            onSuccessCalled = true
        }

        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val video = viewModel.videoLiveData.getOrAwaitValue()
        assertEquals("videoKey", video.key)

        val videoInfo = viewModel.videoInfoLiveData.getOrAwaitValue()
        assertEquals("audioUrl", videoInfo.audioUrl)
        assertEquals("videoUrl", videoInfo.videoUrl)

        assertTrue(onSuccessCalled)

        coVerify { useCase.getVideo(1) }
        coVerify { videoInfoUseCase.getVideoInfo("videoKey") }
    }

    @Test
    fun `getVideoInfo returns failure`() = runTest {
        val mockVideoResponse = VideoResponse(key = "videoKey", site = "YouTube", type = "Trailer")
        coEvery { useCase.getVideo(1) } returns DataResult.Success(mockVideoResponse.toVideo())

        val mockError = Exception("Failed to fetch video info")
        coEvery { videoInfoUseCase.getVideoInfo("videoKey") } returns DataResult.Failure(mockError)

        var onSuccessCalled = false
        viewModel.getVideo(1) {
            onSuccessCalled = true
        }

        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val video = viewModel.videoLiveData.getOrAwaitValue()
        assertEquals("videoKey", video.key)

        val error = viewModel.errorLiveData.getOrAwaitValue()
        assertEquals(mockError, error)

        assertFalse(onSuccessCalled)

        coVerify { useCase.getVideo(1) }
        coVerify { videoInfoUseCase.getVideoInfo("videoKey") }
    }
}