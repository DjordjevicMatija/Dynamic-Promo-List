package rs.ac.bg.etf.dm200157d.mdjlibrary

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.ac.bg.etf.dm200157d.mdjlibrary.databinding.ViewDynamicPromoListBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.MovieList
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.dpToPx


class DynamicPromoList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var itemLayoutOrientation: ItemLayoutOrientation = ItemLayoutOrientation.VERTICAL
    private var titlePosition: TitlePosition = TitlePosition.TITLE_BELOW
    private var circularList: Boolean = false

    private val binding: ViewDynamicPromoListBinding =
        ViewDynamicPromoListBinding.inflate(LayoutInflater.from(context), this)

    private lateinit var movieFocusListener: MovieFocusListener

    private lateinit var adapter: DynamicPromoListAdapter
    private lateinit var smoothScroller: SmoothScroller

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DynamicPromoList,
            0, 0
        ).apply {
            try {
                val layoutOrientationInt =
                    getInt(R.styleable.DynamicPromoList_itemLayoutOrientation, 1)
                itemLayoutOrientation =
                    ItemLayoutOrientation.entries.toTypedArray()[layoutOrientationInt]

                val titlePositionInt = getInt(R.styleable.DynamicPromoList_titlePosition, 0)
                titlePosition = TitlePosition.entries.toTypedArray()[titlePositionInt]

                circularList = getBoolean(R.styleable.DynamicPromoList_circularList, false)
            } finally {
                recycle()
            }
        }
    }

    fun addData(movies: MovieList) {
        adapter.updateMovies(movies)

        if (circularList) {
            val middle = movies.size * CIRCULAR_LIST_SCALE / 2
            val middlePosition =
                (middle) - ((middle) % movies.size)
            binding.recyclerView.scrollToPosition(middlePosition)

            binding.recyclerView.post {
                binding.recyclerView.findViewHolderForAdapterPosition(middlePosition)?.itemView?.requestFocus()
            }
        }

        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val firstVisiblePosition = (binding.recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                if (firstVisiblePosition != null) {
                    binding.recyclerView.findViewHolderForAdapterPosition(firstVisiblePosition)?.itemView?.let { focusedView ->
                        adjustPlayerView(focusedView)
                    }
                }
            }
        })
    }

    fun addListener(listener: MovieFocusListener) {
        movieFocusListener = listener
        adapter = DynamicPromoListAdapter(
            context,
            itemLayoutOrientation,
            titlePosition,
            movieFocusListener,
            circularList
        )
        binding.recyclerView.adapter = adapter
    }

    fun setPlayerView(playerView: View) {
        val playerViewLayoutParams = LayoutParams(
            getPlayerViewWidth(),
            getPlayerViewHeight()
        )
        binding.playerView.addView(playerView, playerViewLayoutParams)
    }

    private fun getPlayerViewWidth(): Int {
        return when (itemLayoutOrientation) {
            ItemLayoutOrientation.HORIZONTAL -> context.dpToPx(HORIZONTAL_WIDTH)
            ItemLayoutOrientation.VERTICAL -> context.dpToPx(VERTICAL_WIDTH)
        }
    }

    private fun getPlayerViewHeight(): Int {
        return when (itemLayoutOrientation) {
            ItemLayoutOrientation.HORIZONTAL -> context.dpToPx(HORIZONTAL_HEIGHT)
            ItemLayoutOrientation.VERTICAL -> context.dpToPx(VERTICAL_HEIGHT)
        }
    }

    private var scrollJob: Job? = null

    fun scrollToFocusedItem(movieId: Int?) {
        movieId?.let {
            adapter.let {
                val position = it.findListIndexFromId(movieId)
                smoothScroller = object : LinearSmoothScroller(context) {
                    override fun getHorizontalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }
                smoothScroller.targetPosition = position
                binding.recyclerView.layoutManager?.startSmoothScroll(smoothScroller)

                scrollJob?.cancel()
                scrollJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.let { focusedView ->
                        adjustPlayerView(focusedView)
                    }
                }
            }
        }
    }

    private fun adjustPlayerView(focusedView: View) {
        val playerView: View = binding.playerView
        val layoutParams = playerView.layoutParams as FrameLayout.LayoutParams

        val location = IntArray(2)
        focusedView.getLocationOnScreen(location)
        val itemX = location[0]
        val itemY = location[1]

        Log.d("Location", "Poster X location: $itemX")
        Log.d("Location", "Poster Y location: $itemY")

        val playerViewLocation = IntArray(2)
        binding.root.getLocationOnScreen(playerViewLocation)
        val playerViewX = playerViewLocation[0]
        val playerViewY = playerViewLocation[1]

        Log.d("Location", "Player X location: $itemX")
        Log.d("Location", "Player Y location: $itemY")

        val relativeX = itemX - playerViewX
        val relativeY = itemY - playerViewY

        layoutParams.leftMargin = relativeX
        layoutParams.topMargin = relativeY

        playerView.layoutParams = layoutParams
        playerView.requestLayout()
    }

    companion object {
        const val HORIZONTAL_WIDTH = 220
        const val HORIZONTAL_HEIGHT = 124
        const val VERTICAL_WIDTH = 120
        const val VERTICAL_HEIGHT = 180
        const val CIRCULAR_LIST_SCALE = 10
    }
}