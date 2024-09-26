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
            // Check if the view is a RecyclerView
            if (view !is RecyclerView) return false

            // Get the ViewHolder for the specified position
            val viewHolder = view.findViewHolderForAdapterPosition(position)
                ?: return false

            // Ensure the item's root view matches the itemMatcher
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