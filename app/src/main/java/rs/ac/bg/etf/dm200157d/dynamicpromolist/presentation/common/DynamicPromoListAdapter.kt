package rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import rs.ac.bg.etf.dm200157d.R
import rs.ac.bg.etf.dm200157d.databinding.DynamicPromoListItemBinding
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Movie
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieList

class DynamicPromoListAdapter(
    private val itemLayoutOrientation: ItemLayoutOrientation,
    private val titlePosition: TitlePosition,
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

        when (titlePosition) {
            TitlePosition.TITLE_BELOW -> {
                holder.binding.movieTitle.visibility = View.VISIBLE

                val layoutParams = holder.binding.movieTitle.layoutParams as RelativeLayout.LayoutParams
                layoutParams.addRule(RelativeLayout.BELOW, holder.binding.imageCardView.id)
                holder.binding.movieTitle.layoutParams = layoutParams
            }
            TitlePosition.TITLE_INVISIBLE -> {
                holder.binding.movieTitle.visibility = View.GONE
            }
            TitlePosition.TITLE_INSIDE -> {
                holder.binding.movieTitle.visibility = View.VISIBLE

                val layoutParams = holder.binding.movieTitle.layoutParams as RelativeLayout.LayoutParams
                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, holder.binding.imageCardView.id)
                holder.binding.movieTitle.layoutParams = layoutParams
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
            binding.moviePoster.loadImage(
                url = "$baseImageUrl${movie.backdropPath}",
                placeholder = R.drawable.placeholder,
                error = R.drawable.poster_not_found
            )
        }
    }
}