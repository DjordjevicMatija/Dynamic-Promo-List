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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieListResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.data.remote.models.MovieResponse
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.DataResult
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.UseCase
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.VideoInfoUseCase
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieDTO
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
    fun `getMovies returns success and updates LiveData`() = runTest {
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
    fun `getMovies returns failure and updates LiveData`() = runTest {
        val mockError = Exception("Failed to fetch movies")

        coEvery { useCase.getMovies() } returns DataResult.Failure(mockError)

        viewModel.getMovies()

        testDispatcher.scheduler.advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()

        val error = viewModel.errorLiveData.getOrAwaitValue()
        assertEquals(mockError, error)

        coVerify { useCase.getMovies() }
    }
}