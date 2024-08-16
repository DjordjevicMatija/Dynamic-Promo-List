package rs.ac.bg.etf.dm200157d.mdjlibrary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import rs.ac.bg.etf.dm200157d.mdjlibrary.databinding.DynamicPromoListItemBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.Movie
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.MovieList
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.dpToPx
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.loadImage

class DynamicPromoListAdapter(
    private val context: Context,
    private val itemLayoutOrientation: ItemLayoutOrientation,
    private val titlePosition: TitlePosition,
    private val movieFocusListener: MovieFocusListener,
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
                posterLayoutParams.width = context.dpToPx(HORIZONTAL_WIDTH)
                posterLayoutParams.height = context.dpToPx(HORIZONTAL_HEIGHT)
            }
            ItemLayoutOrientation.VERTICAL -> {
                posterLayoutParams.width = context.dpToPx(VERTICAL_WIDTH)
                posterLayoutParams.height = context.dpToPx(VERTICAL_HEIGHT)
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
        return if (circularList && movies.isNotEmpty()) movies.size * CIRCULAR_LIST_SCALE else movies.size
    }

    fun updateMovies(newMovies: MovieList) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: DynamicPromoListItemBinding) :
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

            binding.moviePoster.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    movie.id?.let { movieFocusListener.onMovieFocused(it) }
            }
        }
    }

    companion object {
        const val HORIZONTAL_WIDTH = 220
        const val HORIZONTAL_HEIGHT = 124
        const val VERTICAL_WIDTH = 120
        const val VERTICAL_HEIGHT = 180
        const val CIRCULAR_LIST_SCALE = 10
    }
}