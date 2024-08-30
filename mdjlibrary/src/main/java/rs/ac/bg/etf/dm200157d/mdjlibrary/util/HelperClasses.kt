package rs.ac.bg.etf.dm200157d.mdjlibrary.util

import android.util.Property
import android.view.View
import android.view.ViewGroup

class WidthProperty : Property<View, Int>(Int::class.java, "width") {
    override fun get(view: View): Int {
        return view.layoutParams.width
    }

    override fun set(view: View, value: Int) {
        val params = view.layoutParams
        params.width = value
        view.layoutParams = params
    }
}

class HeightProperty : Property<View, Int>(Int::class.java, "height") {
    override fun get(view: View): Int {
        return view.layoutParams.height
    }

    override fun set(view: View, value: Int) {
        val params = view.layoutParams
        params.height = value
        view.layoutParams = params
    }
}
