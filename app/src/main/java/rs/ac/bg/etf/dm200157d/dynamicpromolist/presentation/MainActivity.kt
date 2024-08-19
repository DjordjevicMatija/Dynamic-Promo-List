package rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import rs.ac.bg.etf.dm200157d.databinding.ActivityMainBinding
import rs.ac.bg.etf.dm200157d.mdjlibrary.util.MovieFocusListener


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

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
            override fun onMovieFocused(movieId: Int) {
                mainViewModel.getVideo(movieId)
            }
        })

        mainViewModel.moviesLiveData.observe(this){movieList ->
            Log.d("MainActivity", "Movies: $movieList")
            binding.dynamicPromoList.addData(movieList)
        }

        mainViewModel.errorLiveData.observe(this){error ->
            Log.e("MainActivity", "Error: ${error.message}")
        }

        mainViewModel.videoLiveData.observe(this){video ->
            Log.d("MainActivity", "Video: $video")
        }

        mainViewModel.videoInfoLiveData.observe(this){videoInfo ->
            Log.d("MainActivity", "VideoInfo: $videoInfo")
        }

        mainViewModel.getMovies()
    }
}