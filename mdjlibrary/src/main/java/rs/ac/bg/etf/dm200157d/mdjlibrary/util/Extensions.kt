package rs.ac.bg.etf.dm200157d.mdjlibrary.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImage(
    url: String?,
    placeholder: Int? = null,
    error: Int? = null
) {
    val glideRequest = Glide.with(this.context)
        .load(url ?: "")

    url?.let {
        glideRequest.transform(RoundedCorners(20))
    }

    placeholder?.let {
        glideRequest.apply(RequestOptions().placeholder(it))
    }

    error?.let {
        glideRequest.apply(RequestOptions().error(it))
    }

    glideRequest.into(this)
}

fun Context.dpToPx(dp: Float): Int {
    val density = resources.displayMetrics.density
    return (dp * density).toInt()
}