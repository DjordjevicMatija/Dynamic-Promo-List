package rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation.common

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