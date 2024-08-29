package rs.ac.bg.etf.dm200157d.mdjlibrary.util

interface MovieFocusListener {
    fun onMovieFocused(movieId: Int, hasFocus: Boolean, onSuccess: () -> Unit)
}

