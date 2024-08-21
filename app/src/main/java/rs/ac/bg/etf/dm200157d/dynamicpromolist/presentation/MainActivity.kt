package rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.ac.bg.etf.dm200157d.databinding.ActivityMainBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.R
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.dpToPx

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.dynamicPromoList.addListener(object : MovieFocusListener {
            private var focusJob: Job? = null

            override fun onMovieFocused(movieId: Int, hasFocus: Boolean) {
                focusJob?.cancel()

                if (hasFocus) {
                    binding.dynamicPromoList.scrollToFocusedItem(movieId)

                    focusJob = CoroutineScope(Dispatchers.IO).launch {
                        delay(1000)
                        mainViewModel.getVideo(movieId)
                    }
                } else {
                    playerView.visibility = View.INVISIBLE
                    player.stop()
                    player.clearMediaItems()
                }
            }
        })

        createPlayer()

        binding.dynamicPromoList.setPlayerView(playerView)

        mainViewModel.moviesLiveData.observe(this) { movieList ->
            Log.d("MainActivity", "Movies: $movieList")
            binding.dynamicPromoList.addData(movieList)
        }

        mainViewModel.errorLiveData.observe(this) { error ->
            Log.e("MainActivity", "Error: ${error.message}")
        }

        mainViewModel.videoLiveData.observe(this) { video ->
            Log.d("MainActivity", "Video: $video")
        }

        mainViewModel.videoInfoLiveData.observe(this) { videoInfo ->
            videoInfo.videoUrl?.let { videoInfo.audioUrl?.let { it1 -> initializePlayer(it, it1) } }
            Log.d("MainActivity", "VideoInfo: $videoInfo")
        }

        mainViewModel.getMovies()
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    @OptIn(UnstableApi::class)
    private fun createPlayer() {
        val mediaSourceFactory = DefaultMediaSourceFactory(this)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        playerView = PlayerView(this).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                context.dpToPx(700),
                context.dpToPx(400)
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            useArtwork = false
            setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
            controllerShowTimeoutMs = 5000
            useController = false
            visibility = View.GONE

            setBackgroundResource(R.drawable.highlighted_border)
            setPadding(context.dpToPx(5))
        }
        playerView.player = player
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(videoUrl: String, audioUrl: String) {
        val dataSourceFactory = DefaultDataSource.Factory(this)

        val videoMediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()

        val audioMediaItem = MediaItem.Builder()
            .setUri(audioUrl)
            .setMimeType(MimeTypes.AUDIO_MP4)
            .build()

        val videoMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(videoMediaItem)

        val audioMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(audioMediaItem)

        val mergedSource = MergingMediaSource(videoMediaSource, audioMediaSource)

        player.setMediaSource(mergedSource)

        player.seekTo(1)

        player.prepare()
        player.playWhenReady = true
        playerView.visibility = View.VISIBLE
    }
}