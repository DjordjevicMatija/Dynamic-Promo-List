package rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import rs.ac.bg.etf.dm200157d.R
import rs.ac.bg.etf.dm200157d.databinding.ViewDynamicPromoListBinding
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieList
import rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.util.MovieFocusListener

class DynamicPromoList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var itemLayoutOrientation: ItemLayoutOrientation = ItemLayoutOrientation.VERTICAL
    private var titlePosition: TitlePosition = TitlePosition.TITLE_BELOW

    private val binding: ViewDynamicPromoListBinding =
        ViewDynamicPromoListBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var movieFocusListener: MovieFocusListener

    private lateinit var adapter: DynamicPromoListAdapter

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
            } finally {
                recycle()
            }
        }
    }

    fun addData(movies: MovieList) {
        adapter.updateMovies(movies)
    }

    fun addListener(listener: MovieFocusListener) {
        movieFocusListener = listener
        adapter = DynamicPromoListAdapter(context, itemLayoutOrientation, titlePosition, movieFocusListener)
        binding.recyclerView.adapter = adapter
    }
}