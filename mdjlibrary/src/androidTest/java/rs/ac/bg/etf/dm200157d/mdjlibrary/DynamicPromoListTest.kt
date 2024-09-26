package rs.ac.bg.etf.dm200157d.mdjlibrary

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rs.ac.bg.etf.dm200157d.mdjlibrary.databinding.ActivityTestBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.Movie
import rs.ac.bg.etf.dm200157d.mdjlibrary.testactivity.TestActivity
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.atPosition

@RunWith(AndroidJUnit4::class)
class DynamicPromoListTest {

    private lateinit var binding: ActivityTestBinding

    @get:Rule
    val activityRule = ActivityScenarioRule(TestActivity::class.java)

    @Before
    fun setUp() {
        ActivityScenario.launch(TestActivity::class.java).onActivity { activity ->
            binding = activity.binding
            binding.dynamicPromoList1.addListener(object : MovieFocusListener {

                override fun onMovieFocused(
                    movieId: Int,
                    hasFocus: Boolean,
                    onSuccess: () -> Unit
                ) {
                }
            })
        }
    }

    @Test
    fun testDynamicPromoListVisibility() {
        onView(withId(binding.dynamicPromoList1.id)).check(matches(isDisplayed()))
    }

    @Test
    fun testDynamicPromoListItemCount() {
        val itemCount = 5
        val movies = listOf(
            Movie(1, "Movie 1", "/backdrop1.jpg", "/poster1.jpg", 7.5, "Overview 1"),
            Movie(2, "Movie 2", "/backdrop2.jpg", "/poster2.jpg", 8.0, "Overview 2"),
            Movie(3, "Movie 3", "/backdrop3.jpg", "/poster3.jpg", 6.0, "Overview 3"),
            Movie(4, "Movie 4", "/backdrop4.jpg", "/poster4.jpg", 9.0, "Overview 4"),
            Movie(5, "Movie 5", "/backdrop5.jpg", "/poster5.jpg", 5.0, "Overview 5")
        )

        ActivityScenario.launch(TestActivity::class.java).onActivity { activity ->
            activity.binding.dynamicPromoList1.addListener(object : MovieFocusListener {
                override fun onMovieFocused(
                    movieId: Int,
                    hasFocus: Boolean,
                    onSuccess: () -> Unit
                ) {
                }
            })
            activity.binding.dynamicPromoList1.addData(movies)
        }

        onView((withId(R.id.recyclerView)))
            .check(matches(hasChildCount(itemCount)))
    }


    @Test
    fun testDynamicPromoListFirstItemContent() {
        val movies = listOf(
            Movie(1, "Movie 1", "/backdrop1.jpg", "/poster1.jpg", 7.5, "Overview 1"),
            Movie(2, "Movie 2", "/backdrop2.jpg", "/poster2.jpg", 8.0, "Overview 2")
        )

        ActivityScenario.launch(TestActivity::class.java).onActivity { activity ->
            activity.binding.dynamicPromoList1.addListener(object : MovieFocusListener {
                override fun onMovieFocused(
                    movieId: Int,
                    hasFocus: Boolean,
                    onSuccess: () -> Unit
                ) {
                }
            })
            activity.binding.dynamicPromoList1.addData(movies)
        }

        val recyclerView = binding.dynamicPromoList1.getRecyclerView()
        onView(withId(recyclerView.id))
            .check(matches(atPosition(0, hasDescendant(withText("Movie 1")))))
    }
}