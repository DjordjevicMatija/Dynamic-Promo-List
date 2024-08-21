package rs.ac.bg.etf.dm200157d.mdjlibrary

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import rs.ac.bg.etf.dm200157d.mdjlibrary.databinding.ViewDynamicPromoListBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.MovieList
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener


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
            val middle = movies.size * DynamicPromoListAdapter.CIRCULAR_LIST_SCALE / 2
            val middlePosition =
                (middle) - ((middle) % movies.size)
            binding.recyclerView.scrollToPosition(middlePosition)

            binding.recyclerView.post {
                binding.recyclerView.findViewHolderForAdapterPosition(middlePosition)?.itemView?.requestFocus()
            }
        }
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
        binding.playerView.addView(playerView)
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
                binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.requestFocus()
            }
        }
    }
}