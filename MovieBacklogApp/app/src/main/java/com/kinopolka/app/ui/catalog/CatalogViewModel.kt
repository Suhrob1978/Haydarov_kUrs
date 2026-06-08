package com.kinopolka.app.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinopolka.app.data.model.BacklogStatus
import com.kinopolka.app.data.model.Movie
import com.kinopolka.app.data.repository.BacklogRepository
import com.kinopolka.app.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatalogUiState(
    val loading: Boolean = false,
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val error: String? = null,
    val message: String? = null,
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val backlogRepo: BacklogRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
    }

    fun search() = load()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            movieRepo.catalog(_state.value.query)
                .onSuccess { list -> _state.update { it.copy(loading = false, movies = list) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun addToBacklog(movie: Movie, status: BacklogStatus) {
        viewModelScope.launch {
            backlogRepo.add(movie.id, status)
                .onSuccess { _state.update { it.copy(message = "«${movie.title}» добавлен в бэклог") } }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun consumeMessages() = _state.update { it.copy(error = null, message = null) }
}
