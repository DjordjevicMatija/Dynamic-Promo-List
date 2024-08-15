package rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import rs.ac.bg.etf.dm200157d.R
import rs.ac.bg.etf.dm200157d.databinding.DynamicPromoListItemBinding
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Movie
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieList
import rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.util.MovieFocusListener
import rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.util.dpToPx
import rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.util.loadImage

class DynamicPromoListAdapter(
    private val context: Context,
    private val itemLayoutOrientation: ItemLayoutOrientation,
    private val titlePosition: TitlePosition,
    private val movieFocusListener: MovieFocusListener,
    private var movies: MovieList = emptyList()
) : RecyclerView.Adapter<DynamicPromoListAdapter.ViewHolder>() {

    private lateinit var baseImageUrl: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (!::baseImageUrl.isInitialized) {
            baseImageUrl = parent.context.getString(R.string.base_image_url)
        }

        return ViewHolder(DynamicPromoListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])

        val titleLayoutParams = holder.binding.movieTitle.layoutParams as RelativeLayout.LayoutParams
        val posterLayoutParams = holder.binding.moviePoster.layoutParams as FrameLayout.LayoutParams

        when(itemLayoutOrientation){
            ItemLayoutOrientation.HORIZONTAL -> {
                posterLayoutParams.width = context.dpToPx(HORIZONTAL_WIDTH)
                posterLayoutParams.height = context.dpToPx(HORIZONTAL_HEIGHT)
            }
            ItemLayoutOrientation.VERTICAL -> {
                posterLayoutParams.width = context.dpToPx(VERTICAL_WIDTH)
                posterLayoutParams.height = context.dpToPx(VERTICAL_HEIGHT)
            }
        }

        when (titlePosition) {
            TitlePosition.TITLE_BELOW -> {
                holder.binding.movieTitle.visibility = View.VISIBLE

                titleLayoutParams.addRule(RelativeLayout.BELOW, holder.binding.imageCardView.id)
                holder.binding.movieTitle.layoutParams = titleLayoutParams
            }
            TitlePosition.TITLE_INVISIBLE -> {
                holder.binding.movieTitle.visibility = View.GONE
            }
            TitlePosition.TITLE_INSIDE -> {
                holder.binding.movieTitle.visibility = View.VISIBLE

                titleLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, holder.binding.imageCardView.id)
                holder.binding.movieTitle.layoutParams = titleLayoutParams
            }
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun updateMovies(newMovies: MovieList) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: DynamicPromoListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title

            val posterPath: String? = when(itemLayoutOrientation){
                ItemLayoutOrientation.HORIZONTAL -> {
                    movie.backdropPath
                }

                ItemLayoutOrientation.VERTICAL -> {
                    movie.posterPath
                }
            }
            binding.moviePoster.loadImage(
                url = "$baseImageUrl$posterPath",
                placeholder = R.drawable.placeholder,
                error = R.drawable.poster_not_found
            )

            binding.moviePoster.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus)
                    movie.id?.let { movieFocusListener.onMovieFocused(it) }
            }
        }
    }

    companion object{
        const val HORIZONTAL_WIDTH = 220
        const val HORIZONTAL_HEIGHT = 124
        const val VERTICAL_WIDTH = 120
        const val VERTICAL_HEIGHT = 180
    }
}