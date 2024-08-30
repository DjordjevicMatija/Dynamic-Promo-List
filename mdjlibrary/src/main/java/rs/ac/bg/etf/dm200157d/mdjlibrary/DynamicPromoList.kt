package rs.ac.bg.etf.dm200157d.mdjlibrary

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.ac.bg.etf.dm200157d.mdjlibrary.databinding.DynamicPromoListItemBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.databinding.ViewDynamicPromoListBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.MovieList
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.HeightProperty
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.WidthProperty
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
    private var scrollJob: Job? = null

    private lateinit var player: View
    private var isPlayerExpanded = false

    private lateinit var currentItemView: View

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

        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val firstVisiblePosition =
                    (binding.recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                if (firstVisiblePosition != null) {
                    scrollJob?.cancel()
                    scrollJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        binding.recyclerView.findViewHolderForAdapterPosition(firstVisiblePosition)?.itemView?.let { focusedView ->
                            currentItemView = focusedView
                        }
                    }
                }
            }
        })
    }

    fun addListener(listener: MovieFocusListener) {
        movieFocusListener = listener
        adapter = DynamicPromoListAdapter(
            this,
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
            context.dpToPx(PLAYER_WIDTH),
            context.dpToPx(PLAYER_HEIGHT)
        )
        player = playerView
        binding.playerView.addView(playerView, playerViewLayoutParams)
    }

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
                    delay(200)
                    binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.let { focusedView ->
                        currentItemView = focusedView
                    }
                }
            }
        }
    }

    private fun adjustPlayerView(focusedView: View) {
        val itemBinding = DynamicPromoListItemBinding.bind(focusedView)

        val playerView: View = binding.playerView
        val layoutParams = playerView.layoutParams as LayoutParams

        val location = IntArray(2)
        itemBinding.imageCardView.getLocationOnScreen(location)
        val itemX = location[0]
        val itemY = location[1]

        val playerViewLocation = IntArray(2)
        binding.root.getLocationOnScreen(playerViewLocation)
        val playerViewX = playerViewLocation[0]
        val playerViewY = playerViewLocation[1]

        val relativeX = itemX - playerViewX
        val relativeY = itemY - playerViewY

        val titleHeight = itemBinding.movieTitle.height
        val titleTopMargin = (itemBinding.movieTitle.layoutParams as MarginLayoutParams).topMargin
        val titleBottomMargin =
            (itemBinding.movieTitle.layoutParams as MarginLayoutParams).bottomMargin

        if (titlePosition == TitlePosition.TITLE_BELOW) {
            layoutParams.bottomMargin = relativeY + titleHeight + titleTopMargin + titleBottomMargin
        } else {
            layoutParams.bottomMargin = relativeY
        }
        layoutParams.topMargin = relativeY
        layoutParams.leftMargin = relativeX

        playerView.layoutParams = layoutParams
        playerView.requestLayout()
    }

    fun expandPlayerView() {
        if (!isPlayerExpanded) {
            animatePlayerViewExpansion()
        }
    }

    private fun animatePlayerViewExpansion() {
        val itemBinding = DynamicPromoListItemBinding.bind(currentItemView)

        val expandedWidth = context.dpToPx(PLAYER_WIDTH)
        val expandedHeight = context.dpToPx(PLAYER_HEIGHT)

        val moviePoster = itemBinding.moviePoster
        val itemView = itemBinding.itemLayout

        val currentTopMargin = (itemView.layoutParams as MarginLayoutParams).topMargin
        val currentBottomMargin = (itemView.layoutParams as MarginLayoutParams).bottomMargin
        val additionalHeight = (expandedHeight - moviePoster.height) / 2

        val posterWidthAnimator =
            ObjectAnimator.ofInt(moviePoster, WidthProperty(), moviePoster.width, expandedWidth)
        val posterHeightAnimator =
            ObjectAnimator.ofInt(moviePoster, HeightProperty(), moviePoster.height, expandedHeight)

        val topMarginAnimator =
            ValueAnimator.ofInt(currentTopMargin, currentTopMargin - additionalHeight)
        topMarginAnimator.addUpdateListener { animator ->
            val params = itemView.layoutParams as MarginLayoutParams
            params.topMargin = animator.animatedValue as Int
            itemView.layoutParams = params
        }

        val bottomMarginAnimator =
            ValueAnimator.ofInt(currentBottomMargin, currentBottomMargin - additionalHeight)
        bottomMarginAnimator.addUpdateListener { animator ->
            val params = itemView.layoutParams as MarginLayoutParams
            params.bottomMargin = animator.animatedValue as Int
            itemView.layoutParams = params
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            posterWidthAnimator,
            posterHeightAnimator,
            topMarginAnimator,
            bottomMarginAnimator
        )
        animatorSet.duration = 300
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                scrollJob?.cancel()
                scrollJob = CoroutineScope(Dispatchers.Main).launch {
                    val viewRect = Rect()
                    currentItemView.getHitRect(viewRect)
                    adjustRecyclerViewScroll(viewRect)

                    delay(200)

                    adjustPlayerView(currentItemView)
                    player.visibility = View.VISIBLE
                    itemBinding.moviePoster.alpha = 0f
                }
            }
        })

        animatorSet.start()
        isPlayerExpanded = true
    }

    private fun adjustRecyclerViewScroll(viewRect: Rect) {
        binding.recyclerView.post {
            val recyclerRect = Rect()
            binding.recyclerView.getHitRect(recyclerRect)
            if (!recyclerRect.contains(viewRect)) {
                val dx = when {
                    viewRect.right > recyclerRect.right -> viewRect.right - recyclerRect.right + 100
                    viewRect.left < recyclerRect.left -> viewRect.left - recyclerRect.left
                    else -> 0
                }
                binding.recyclerView.smoothScrollBy(dx, 0)
            }
        }
    }

    fun collapsePlayerView() {
        if (isPlayerExpanded) {
            animatePlayerViewCollapse()
        }
    }

    private fun animatePlayerViewCollapse() {
        val itemBinding = DynamicPromoListItemBinding.bind(currentItemView)

        val collapsedWidth =
            if (itemLayoutOrientation == ItemLayoutOrientation.HORIZONTAL) context.dpToPx(
                HORIZONTAL_WIDTH
            ) else context.dpToPx(VERTICAL_WIDTH)
        val collapsedHeight =
            if (itemLayoutOrientation == ItemLayoutOrientation.HORIZONTAL) context.dpToPx(
                HORIZONTAL_HEIGHT
            ) else context.dpToPx(VERTICAL_HEIGHT)

        val moviePoster = itemBinding.moviePoster
        val itemView = itemBinding.itemLayout

        val expandedHeight = moviePoster.height
        val currentTopMargin = (itemView.layoutParams as MarginLayoutParams).topMargin
        val currentBottomMargin = (itemView.layoutParams as MarginLayoutParams).bottomMargin
        val additionalHeight = (expandedHeight - collapsedHeight) / 2

        val posterWidthAnimator =
            ObjectAnimator.ofInt(moviePoster, WidthProperty(), moviePoster.width, collapsedWidth)
        val posterHeightAnimator =
            ObjectAnimator.ofInt(moviePoster, HeightProperty(), moviePoster.height, collapsedHeight)

        val topMarginAnimator =
            ValueAnimator.ofInt(currentTopMargin, currentTopMargin + additionalHeight)
        topMarginAnimator.addUpdateListener { animator ->
            val params = itemView.layoutParams as MarginLayoutParams
            params.topMargin = animator.animatedValue as Int
            itemView.layoutParams = params
        }

        val bottomMarginAnimator =
            ValueAnimator.ofInt(currentBottomMargin, currentBottomMargin + additionalHeight)
        bottomMarginAnimator.addUpdateListener { animator ->
            val params = itemView.layoutParams as MarginLayoutParams
            params.bottomMargin = animator.animatedValue as Int
            itemView.layoutParams = params
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            posterWidthAnimator,
            posterHeightAnimator,
            topMarginAnimator,
            bottomMarginAnimator
        )
        animatorSet.duration = 300
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        itemBinding.moviePoster.alpha = 1f
        player.visibility = View.INVISIBLE
        animatorSet.start()
        isPlayerExpanded = false
    }

    companion object {
        const val HORIZONTAL_WIDTH = 220
        const val HORIZONTAL_HEIGHT = 124
        const val VERTICAL_WIDTH = 120
        const val VERTICAL_HEIGHT = 180
        const val CIRCULAR_LIST_SCALE = 10
        const val PLAYER_WIDTH = 330
        const val PLAYER_HEIGHT = 186
    }
}