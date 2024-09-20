package rs.ac.bg.etf.dm200157d.mdjlibrary

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import rs.ac.bg.etf.dm200157d.mdjlibrary.databinding.DynamicPromoListItemBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.Movie
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.MovieList
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.loadImage

class DynamicPromoListAdapter(
    private val dynamicPromoList: DynamicPromoList,
    private val context: Context,
    private val itemLayoutOrientation: ItemLayoutOrientation,
    private val titlePosition: TitlePosition,
    private val movieFocusListener: MovieFocusListener,
    private val borderColor: Int,
    private val circularList: Boolean,
    private val titleSize: Float,
    private val titleColor: Int,
    private var titleFont: Typeface?,
    private var posterWidth: Int,
    private var posterHeight: Int,
    private var playerHeight: Int,
    private val sideTitleSize: Float,
    private val sideTitleColor: Int,
    private var sideTitleFont: Typeface?,
    private val overviewSize: Float,
    private val overviewColor: Int,
    private var overviewFont: Typeface?,
    private var movies: MovieList = emptyList()
) : RecyclerView.Adapter<DynamicPromoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DynamicPromoListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        setMoviePosterLayout(binding)
        setMovieTitleLayout(binding)
        setOverviewLayout(binding)

        return ViewHolder(binding)
    }

    private fun setMoviePosterLayout(binding: DynamicPromoListItemBinding) {
        val posterLayoutParams = binding.moviePoster.layoutParams as FrameLayout.LayoutParams

        posterLayoutParams.width = posterWidth
        posterLayoutParams.height = posterHeight

        binding.moviePoster.layoutParams = posterLayoutParams
    }

    private fun setMovieTitleLayout(binding: DynamicPromoListItemBinding) {
        val titleLayoutParams = binding.movieTitle.layoutParams as RelativeLayout.LayoutParams

        when (titlePosition) {
            TitlePosition.TITLE_BELOW -> {
                binding.movieTitle.visibility = View.VISIBLE

                titleLayoutParams.addRule(RelativeLayout.BELOW, binding.imageCardView.id)
                binding.movieTitle.layoutParams = titleLayoutParams
            }

            TitlePosition.TITLE_INVISIBLE -> {
                binding.movieTitle.visibility = View.GONE
            }

            TitlePosition.TITLE_INSIDE -> {
                binding.movieTitle.visibility = View.VISIBLE

                titleLayoutParams.addRule(
                    RelativeLayout.ALIGN_BOTTOM,
                    binding.imageCardView.id
                )
                binding.movieTitle.layoutParams = titleLayoutParams
            }
        }
        binding.movieTitle.layoutParams = titleLayoutParams
    }

    private fun setOverviewLayout(binding: DynamicPromoListItemBinding) {
        val sideLayoutParams = binding.sideLayout.layoutParams

        sideLayoutParams.width = 0
        sideLayoutParams.height = playerHeight
        binding.sideLayout.layoutParams = sideLayoutParams
        binding.sideLayout.visibility = View.INVISIBLE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualPosition = position % movies.size
        holder.bind(movies[actualPosition])
    }

    override fun getItemCount(): Int {
        return if (circularList && movies.isNotEmpty()) movies.size * DynamicPromoList.CIRCULAR_LIST_SCALE else movies.size
    }

    fun updateMovies(newMovies: MovieList) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: DynamicPromoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {

            bindMoviePoster(binding, movie)
            bindMovieTitle(binding, movie)
            bindOverview(binding, movie)

            binding.moviePoster.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    dynamicPromoList.animatePlayerViewCollapse()
                }
                movie.id?.let {
                    movieFocusListener.onMovieFocused(it, hasFocus) {
                        if (hasFocus) {
                            dynamicPromoList.animateItemTransition(it)
                        }
                    }
                }
            }
        }
    }

    private fun bindMovieTitle(binding: DynamicPromoListItemBinding, movie: Movie) {
        binding.movieTitle.text = movie.title
        binding.movieTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
        binding.movieTitle.setTextColor(titleColor)
        binding.movieTitle.typeface = titleFont

        binding.movieTitle.post {
            val paint = binding.movieTitle.paint
            val textHeight = paint.fontMetricsInt.descent - paint.fontMetricsInt.ascent

            val totalHeight = textHeight * 2

            val layoutParams = binding.movieTitle.layoutParams
            layoutParams.height =
                totalHeight + binding.movieTitle.paddingTop + binding.movieTitle.paddingBottom
            binding.movieTitle.layoutParams = layoutParams
        }
    }

    private fun bindMoviePoster(binding: DynamicPromoListItemBinding, movie: Movie) {
        val posterPath: String? = when (itemLayoutOrientation) {
            ItemLayoutOrientation.HORIZONTAL -> movie.backdropPath

            ItemLayoutOrientation.VERTICAL -> movie.posterPath
        }
        binding.moviePoster.loadImage(
            url = "$posterPath",
            placeholder = R.drawable.placeholder,
            error = R.drawable.poster_not_found
        )

        val imageSelector = StateListDrawable()
        imageSelector.addState(intArrayOf(android.R.attr.state_focused), ColorDrawable(borderColor))
        imageSelector.addState(
            intArrayOf(),
            ContextCompat.getDrawable(context, R.drawable.default_border)
        )

        binding.moviePoster.background = imageSelector
    }

    private fun bindOverview(binding: DynamicPromoListItemBinding, movie: Movie) {
        binding.sideTitle.text = movie.title
        binding.sideTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sideTitleSize)
        binding.sideTitle.setTextColor(sideTitleColor)
        binding.sideTitle.typeface = sideTitleFont
        binding.sideTitle.isSelected = true
        binding.sideTitle.setHorizontallyScrolling(true)

        binding.sideRating.text = String.format("Rating: %.1f", movie.voteAverage)
        binding.sideRating.setTextSize(TypedValue.COMPLEX_UNIT_PX, overviewSize)
        binding.sideRating.setTextColor(overviewColor)
        binding.sideRating.typeface = overviewFont

        binding.sideOverview.text = movie.overview
        binding.sideOverview.setTextSize(TypedValue.COMPLEX_UNIT_PX, overviewSize)
        binding.sideOverview.setTextColor(overviewColor)
        binding.sideOverview.typeface = overviewFont

        binding.sideOverview.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val paint = binding.sideOverview.paint
                val lineHeight = paint.fontMetricsInt.bottom - paint.fontMetricsInt.top
                val viewHeight = binding.sideTitle.top + playerHeight - binding.sideOverview.top

                binding.sideOverview.maxLines = viewHeight / lineHeight
                binding.sideOverview.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun findListIndicesFromId(movieId: Int?): List<Int> {
        val originalPositions = movies.mapIndexedNotNull { index, movie ->
            if (movie.id == movieId) index else null
        }

        return if (circularList && originalPositions.isNotEmpty()) {
            originalPositions.flatMap { originalIndex ->
                (0 until DynamicPromoList.CIRCULAR_LIST_SCALE).map { scaleFactor ->
                    originalIndex + (movies.size * scaleFactor)
                }
            }
        } else {
            originalPositions
        }
    }
}