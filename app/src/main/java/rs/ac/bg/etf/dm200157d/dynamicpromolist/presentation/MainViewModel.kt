package rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.DataResult
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.UseCase
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.Video
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.util.toLibMovie
import rs.ac.bg.etf.dm200157d.mdjlibrary.entities.MovieList
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {

    private val _moviesLiveData = MutableLiveData<MovieList>()
    val moviesLiveData: LiveData<MovieList> get() = _moviesLiveData

    private val _errorLiveData = MutableLiveData<Exception>()
    val errorLiveData: LiveData<Exception> get() = _errorLiveData

    private val _videoLiveData = MutableLiveData<Video>()
    val videoLiveData: LiveData<Video> get() = _videoLiveData

    fun getMovies() {
        viewModelScope.launch {
            when (val result = useCase.getMovies()) {
                is DataResult.Success -> _moviesLiveData.postValue(result.data.map { it.toLibMovie() })
                is DataResult.Failure -> _errorLiveData.postValue(result.error)
            }
        }
    }

    fun getVideo(id: Int){
        viewModelScope.launch {
            when(val result = useCase.getVideo(id)){
                is DataResult.Success -> _videoLiveData.postValue(result.data)
                is DataResult.Failure -> _errorLiveData.postValue(result.error)
            }
        }
    }
}