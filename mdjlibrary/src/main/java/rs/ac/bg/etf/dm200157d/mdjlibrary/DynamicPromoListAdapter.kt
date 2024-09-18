package rs.ac.bg.etf.dm200157d.mdjlibrary

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import rs.ac.bg.etf.dm200157d.mdjlibrary.databinding.DynamicPromoListItemBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.Movie
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.MovieList
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.dpToPx
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.loadImage

class DynamicPromoListAdapter(
    private val dynamicPromoList: DynamicPromoList,
    private val context: Context,
    private val itemLayoutOrientation: ItemLayoutOrientation,
    private val titlePosition: TitlePosition,
    private val movieFocusListener: MovieFocusListener,
    private val borderColor: Int,
    private val circularList: Boolean = false,
    private var movies: MovieList = emptyList()
) : RecyclerView.Adapter<DynamicPromoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DynamicPromoListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        val posterLayoutParams = binding.moviePoster.layoutParams as FrameLayout.LayoutParams
        val titleLayoutParams = binding.movieTitle.layoutParams as RelativeLayout.LayoutParams

        when (itemLayoutOrientation) {
            ItemLayoutOrientation.HORIZONTAL -> {
                posterLayoutParams.width = context.dpToPx(DynamicPromoList.HORIZONTAL_WIDTH)
                posterLayoutParams.height = context.dpToPx(DynamicPromoList.HORIZONTAL_HEIGHT)
            }

            ItemLayoutOrientation.VERTICAL -> {
                posterLayoutParams.width = context.dpToPx(DynamicPromoList.VERTICAL_WIDTH)
                posterLayoutParams.height = context.dpToPx(DynamicPromoList.VERTICAL_HEIGHT)
            }
        }
        binding.moviePoster.layoutParams = posterLayoutParams

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

        return ViewHolder(binding)
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
            binding.movieTitle.text = movie.title

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
            imageSelector.addState(intArrayOf(), ContextCompat.getDrawable(context, R.drawable.default_border))

            binding.moviePoster.background = imageSelector

            binding.moviePoster.setOnFocusChangeListener { _, hasFocus ->
                if(!hasFocus){
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