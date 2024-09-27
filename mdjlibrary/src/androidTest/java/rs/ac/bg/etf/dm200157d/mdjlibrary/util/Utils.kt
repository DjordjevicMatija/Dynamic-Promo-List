package rs.ac.bg.etf.dm200157d.mdjlibrary.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import androidx.test.espresso.IdlingResource

fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("RecyclerView item at position $position: ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: View): Boolean {
            if (view !is RecyclerView) return false

            val viewHolder = view.findViewHolderForAdapterPosition(position)
                ?: return false

            return itemMatcher.matches(viewHolder.itemView)
        }
    }
}



class SimpleIdlingResource : IdlingResource {

    @Volatile
    private var isIdleNow = true
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "SimpleIdlingResource"

    override fun isIdleNow(): Boolean = isIdleNow

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }

    fun setIdleState(isIdle: Boolean) {
        isIdleNow = isIdle
        if (isIdleNow && resourceCallback != null) {
            resourceCallback!!.onTransitionToIdle()
        }
    }
}