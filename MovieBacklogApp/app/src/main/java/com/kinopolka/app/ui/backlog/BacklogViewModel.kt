package com.kinopolka.app.ui.backlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinopolka.app.data.model.BacklogItem
import com.kinopolka.app.data.model.BacklogStatus
import com.kinopolka.app.data.model.UpdateBacklogRequest
import com.kinopolka.app.data.repository.BacklogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BacklogUiState(
    val loading: Boolean = false,
    val filter: BacklogStatus? = null,
    val items: List<BacklogItem> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class BacklogViewModel @Inject constructor(
    private val repo: BacklogRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BacklogUiState())
    val state: StateFlow<BacklogUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun setFilter(status: BacklogStatus?) {
        _state.update { it.copy(filter = status) }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            repo.list(_state.value.filter)
                .onSuccess { list -> _state.update { it.copy(loading = false, items = list) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        }
    }

    fun update(item: BacklogItem, status: BacklogStatus, rating: Int, note: String) {
        viewModelScope.launch {
            repo.update(item.id, UpdateBacklogRequest(status = status, userRating = rating, note = note))
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun remove(item: BacklogItem) {
        viewModelScope.launch {
            repo.remove(item.id)
                .onSuccess { load() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun consumeError() = _state.update { it.copy(error = null) }
}
