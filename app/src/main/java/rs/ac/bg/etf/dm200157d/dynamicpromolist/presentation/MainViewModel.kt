package rs.ac.bg.etf.dm200157d.dynamicpromolist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.DataResult
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.UseCase
import rs.ac.bg.etf.dm200157d.dynamicpromolist.domain.entities.MovieList
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {

    private val _moviesLiveData = MutableLiveData<MovieList>()
    val moviesLiveData: LiveData<MovieList> get() = _moviesLiveData

    private val _errorLiveData = MutableLiveData<Exception>()
    val errorLiveData: LiveData<Exception> get() = _errorLiveData

    fun getMovies() {
        viewModelScope.launch {
            when (val result = useCase.getMovies()) {
                is DataResult.Success -> _moviesLiveData.postValue(result.data)
                is DataResult.Failure -> _errorLiveData.postValue(result.error)
            }
        }
    }
}