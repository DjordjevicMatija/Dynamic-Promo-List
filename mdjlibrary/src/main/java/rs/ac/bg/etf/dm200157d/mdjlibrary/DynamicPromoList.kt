package rs.ac.bg.etf.dm200157d.mdjlibrary

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private var borderColor: Int = ContextCompat.getColor(context, R.color.highlighted_border_color)

    private var textSize: Float = 16f
    private var textColor: Int = Color.BLACK
    private var textFont: Typeface? = null

    private var itemWidth: Int = context.dpToPx(VERTICAL_WIDTH)
    private var itemHeight: Int = context.dpToPx(VERTICAL_HEIGHT)
    private var playerWidth: Int = context.dpToPx(PLAYER_WIDTH)
    private var playerHeight: Int = context.dpToPx(PLAYER_HEIGHT)

    private val binding: ViewDynamicPromoListBinding =
        ViewDynamicPromoListBinding.inflate(LayoutInflater.from(context), this)

    private lateinit var movieFocusListener: MovieFocusListener

    private lateinit var adapter: DynamicPromoListAdapter
    private lateinit var smoothScroller: SmoothScroller
    private var scrollJob: Job? = null

    private lateinit var currentItemView: View
    private lateinit var previousItemView: View
    private var currentPosition: Int = 0
    private var currentMovieId: Int = 0

    private var currentAnimatorSet: AnimatorSet? = null
    private var isAnimationCancelled = false

    private var collapsing: Boolean = false

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

                borderColor = getColor(
                    R.styleable.DynamicPromoList_borderColor,
                    ContextCompat.getColor(context, R.color.highlighted_border_color)
                )

                val rawTextSize = getDimension(R.styleable.DynamicPromoList_textSize, textSize)
                textSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    rawTextSize,
                    resources.displayMetrics
                )

                textColor = getColor(R.styleable.DynamicPromoList_textColor, textColor)

                textFont = getFont(R.styleable.DynamicPromoList_textFont)

                var defaultWidth = itemWidth
                if (itemLayoutOrientation == ItemLayoutOrientation.HORIZONTAL)
                    defaultWidth = context.dpToPx(HORIZONTAL_WIDTH)
                itemWidth = getDimension(
                    R.styleable.DynamicPromoList_itemWidth,
                    defaultWidth.toFloat()
                ).toInt()

                var defaultHeight = itemHeight
                if (itemLayoutOrientation == ItemLayoutOrientation.HORIZONTAL)
                    defaultHeight = context.dpToPx(HORIZONTAL_HEIGHT)
                itemHeight = getDimension(
                    R.styleable.DynamicPromoList_itemHeight,
                    defaultHeight.toFloat()
                ).toInt()

                playerWidth = getDimension(
                    R.styleable.DynamicPromoList_playerWidth,
                    playerWidth.toFloat()
                ).toInt()

                playerHeight = getDimension(
                    R.styleable.DynamicPromoList_playerHeight,
                    playerHeight.toFloat()
                ).toInt()
            } finally {
                recycle()
            }
        }
        applyBorderColor()
    }

    private fun applyBorderColor() {
        // Apply color to the shape drawable
        val highlightedBorder =
            ContextCompat.getDrawable(context, R.drawable.highlighted_border) as GradientDrawable
        highlightedBorder.setStroke(context.dpToPx(5f), borderColor)

        binding.playerView.foreground = highlightedBorder
    }

    fun addData(movies: MovieList) {
        adapter.updateMovies(movies)

        movies[0].id?.let {
            currentMovieId = it
        }

        if (circularList) {
            val middle = movies.size * CIRCULAR_LIST_SCALE / 2
            val middlePosition =
                (middle) - ((middle) % movies.size)
            binding.recyclerView.scrollToPosition(middlePosition)

            binding.recyclerView.post {
                binding.recyclerView.findViewHolderForAdapterPosition(middlePosition)?.itemView?.requestFocus()
            }

            currentPosition = middlePosition
        }

        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val firstVisiblePosition =
                    (binding.recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                if (firstVisiblePosition != null) {
                    binding.recyclerView.findViewHolderForAdapterPosition(firstVisiblePosition)?.itemView?.let { focusedView ->
                        currentItemView = focusedView
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
            borderColor,
            circularList,
            textSize,
            textColor,
            textFont,
            itemWidth,
            itemHeight
        )
        binding.recyclerView.adapter = adapter
    }

    fun setPlayerView(playerView: View) {
        val playerViewLayoutParams = LayoutParams(
            playerWidth, playerHeight
        )
        binding.playerView.addView(playerView, playerViewLayoutParams)
        binding.playerView.visibility = View.INVISIBLE
    }

    fun scrollToFocusedItem(movieId: Int?) {
        movieId?.let {
            currentMovieId = movieId
            val positions = adapter.findListIndicesFromId(movieId)

            if (positions.isNotEmpty()) {
                val targetPosition = positions.minByOrNull { position ->
                    kotlin.math.abs(position - currentPosition)
                } ?: currentPosition

                currentPosition = targetPosition

                smoothScroller = object : LinearSmoothScroller(context) {
                    override fun getHorizontalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }
                smoothScroller.targetPosition = currentPosition
                binding.recyclerView.layoutManager?.startSmoothScroll(smoothScroller)

                binding.recyclerView.findViewHolderForAdapterPosition(currentPosition)?.itemView?.let { focusedView ->
                    if (::currentItemView.isInitialized) {
                        previousItemView = currentItemView
                    }
                    currentItemView = focusedView
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

    fun animateItemTransition(movieId: Int) {

        if (!::currentItemView.isInitialized || currentMovieId != movieId) {
            return
        }

        isAnimationCancelled = true
        currentAnimatorSet?.cancel()
        scrollJob?.cancel()
        isAnimationCancelled = false

        val animatorSet = AnimatorSet()

        if (::previousItemView.isInitialized) {
            val previousItemBinding = DynamicPromoListItemBinding.bind(previousItemView)

            previousItemBinding.moviePoster.alpha = 1f
            if (titlePosition != TitlePosition.TITLE_INVISIBLE) {
                previousItemBinding.movieTitle.visibility = View.VISIBLE
            }

            val collapsedWidth = itemWidth

            val collapsedHeight = itemHeight

            val previousMoviePoster = previousItemBinding.moviePoster
            val previousItem = previousItemBinding.itemLayout

            val previousExpandedHeight = previousMoviePoster.height
            val previousTopMargin = (previousItem.layoutParams as MarginLayoutParams).topMargin
            val previousBottomMargin =
                (previousItem.layoutParams as MarginLayoutParams).bottomMargin
            val previousAdditionalHeight = (previousExpandedHeight - collapsedHeight) / 2

            val previousPosterWidthAnimator =
                ObjectAnimator.ofInt(
                    previousMoviePoster,
                    WidthProperty(),
                    previousMoviePoster.width,
                    collapsedWidth
                )
            val previousPosterHeightAnimator =
                ObjectAnimator.ofInt(
                    previousMoviePoster,
                    HeightProperty(),
                    previousMoviePoster.height,
                    collapsedHeight
                )

            val previousTopMarginAnimator =
                ValueAnimator.ofInt(previousTopMargin, previousTopMargin + previousAdditionalHeight)
            previousTopMarginAnimator.addUpdateListener { animator ->
                val params = previousItem.layoutParams as MarginLayoutParams
                params.topMargin = animator.animatedValue as Int
                previousItem.layoutParams = params
            }

            val previousBottomMarginAnimator =
                ValueAnimator.ofInt(
                    previousBottomMargin,
                    previousBottomMargin + previousAdditionalHeight
                )
            previousBottomMarginAnimator.addUpdateListener { animator ->
                val params = previousItem.layoutParams as MarginLayoutParams
                params.bottomMargin = animator.animatedValue as Int
                previousItem.layoutParams = params
            }

            animatorSet.playTogether(
                previousPosterWidthAnimator,
                previousPosterHeightAnimator,
                previousTopMarginAnimator,
                previousBottomMarginAnimator
            )
        }

        val currentItemBinding = DynamicPromoListItemBinding.bind(currentItemView)

        val expandedWidth = playerWidth
        val expandedHeight = playerHeight

        val currentMoviePoster = currentItemBinding.moviePoster
        val currentItemView = currentItemBinding.itemLayout

        val currentTopMargin = (currentItemView.layoutParams as MarginLayoutParams).topMargin
        val currentBottomMargin = (currentItemView.layoutParams as MarginLayoutParams).bottomMargin
        val currentAdditionalHeight = (expandedHeight - currentMoviePoster.height) / 2

        val currentPosterWidthAnimator =
            ObjectAnimator.ofInt(
                currentMoviePoster,
                WidthProperty(),
                currentMoviePoster.width,
                expandedWidth
            )
        val currentPosterHeightAnimator =
            ObjectAnimator.ofInt(
                currentMoviePoster,
                HeightProperty(),
                currentMoviePoster.height,
                expandedHeight
            )

        val currentTopMarginAnimator =
            ValueAnimator.ofInt(currentTopMargin, currentTopMargin - currentAdditionalHeight)
        currentTopMarginAnimator.addUpdateListener { animator ->
            val params = currentItemView.layoutParams as MarginLayoutParams
            params.topMargin = animator.animatedValue as Int
            currentItemView.layoutParams = params
        }

        val currentBottomMarginAnimator =
            ValueAnimator.ofInt(currentBottomMargin, currentBottomMargin - currentAdditionalHeight)
        currentBottomMarginAnimator.addUpdateListener { animator ->
            val params = currentItemView.layoutParams as MarginLayoutParams
            params.bottomMargin = animator.animatedValue as Int
            currentItemView.layoutParams = params
        }

        animatorSet.playTogether(
            currentPosterWidthAnimator,
            currentPosterHeightAnimator,
            currentTopMarginAnimator,
            currentBottomMarginAnimator
        )

        animatorSet.duration = 300
        animatorSet.interpolator = AccelerateDecelerateInterpolator()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isAnimationCancelled) {
                    super.onAnimationEnd(animation)

                    val viewRect = Rect()
                    currentItemView.getHitRect(viewRect)

                    scrollJob?.cancel()
                    scrollJob = CoroutineScope(Dispatchers.Main).launch {
                        adjustRecyclerViewScroll(viewRect) {
                            adjustPlayerView(currentItemView)

                            if (!collapsing) {
                                binding.playerView.visibility = View.VISIBLE
                                currentItemBinding.moviePoster.alpha = 0f
                            }
                        }
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)

                binding.playerView.visibility = View.INVISIBLE
                currentItemBinding.moviePoster.alpha = 1f
                if (titlePosition != TitlePosition.TITLE_INVISIBLE) {
                    currentItemBinding.movieTitle.visibility = View.VISIBLE
                }
            }

        })

        currentAnimatorSet = animatorSet
        animatorSet.start()
        if (titlePosition != TitlePosition.TITLE_INVISIBLE) {
            currentItemBinding.movieTitle.visibility = View.INVISIBLE
        }
    }

    private fun adjustRecyclerViewScroll(viewRect: Rect, onScrollEnd: () -> Unit) {
        binding.recyclerView.post {
            val recyclerRect = Rect()
            binding.recyclerView.getHitRect(recyclerRect)
            val dx = when {
                viewRect.right > recyclerRect.right -> viewRect.right - recyclerRect.right + 100
                viewRect.left < recyclerRect.left -> viewRect.left - recyclerRect.left
                else -> 0
            }

            if (dx != 0) {
                binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            recyclerView.removeOnScrollListener(this)
                            onScrollEnd()
                        }
                    }
                })

                binding.recyclerView.smoothScrollBy(dx, 0)
            } else {
                onScrollEnd()
            }
        }
    }

    fun animatePlayerViewCollapse() {

        if (!::currentItemView.isInitialized) {
            return
        }

        val itemBinding = DynamicPromoListItemBinding.bind(currentItemView)

        val collapsedWidth = itemWidth
        val collapsedHeight = itemHeight

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

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (titlePosition != TitlePosition.TITLE_INVISIBLE) {
                    itemBinding.movieTitle.visibility = View.VISIBLE
                }
                collapsing = false
            }
        })


        isAnimationCancelled = true
        currentAnimatorSet?.cancel()
        scrollJob?.cancel()

        itemBinding.moviePoster.alpha = 1f
        binding.playerView.visibility = View.INVISIBLE

        collapsing = true
        animatorSet.start()
    }

    companion object {
        const val HORIZONTAL_WIDTH = 220f
        const val HORIZONTAL_HEIGHT = 124f
        const val VERTICAL_WIDTH = 120f
        const val VERTICAL_HEIGHT = 180f
        const val CIRCULAR_LIST_SCALE = 10
        const val PLAYER_WIDTH = 330f
        const val PLAYER_HEIGHT = 186f
    }
}