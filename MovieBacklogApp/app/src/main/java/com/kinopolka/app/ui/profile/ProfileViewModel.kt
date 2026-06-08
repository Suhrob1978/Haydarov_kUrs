package com.kinopolka.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinopolka.app.data.AppEventBus
import com.kinopolka.app.data.model.BacklogStats
import com.kinopolka.app.data.model.UserDto
import com.kinopolka.app.data.repository.AuthRepository
import com.kinopolka.app.data.repository.BacklogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserDto? = null,
    val stats: BacklogStats? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val backlogRepo: BacklogRepository,
    private val events: AppEventBus,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(user = authRepo.currentUser()))
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            events.backlogChanged.collect { refresh() }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            backlogRepo.stats().onSuccess { stats ->
                _state.update { it.copy(stats = stats) }
            }
        }
    }

    fun logout() = authRepo.logout()
}
