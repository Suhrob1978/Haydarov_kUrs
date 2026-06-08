package com.kinopolka.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinopolka.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (!validate(email, password)) return
        run({ repo.login(email, password) }, onSuccess)
    }

    fun register(email: String, password: String, name: String, onSuccess: () -> Unit) {
        if (!validate(email, password)) return
        if (name.isBlank()) {
            _state.update { it.copy(error = "Введите имя") }
            return
        }
        run({ repo.register(email, password, name) }, onSuccess)
    }

    fun clearError() = _state.update { it.copy(error = null) }

    private fun validate(email: String, password: String): Boolean {
        if (email.isBlank() || !email.contains("@")) {
            _state.update { it.copy(error = "Введите корректную почту") }
            return false
        }
        if (password.length < 6) {
            _state.update { it.copy(error = "Пароль не короче 6 символов") }
            return false
        }
        return true
    }

    private fun run(action: suspend () -> Result<*>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val result = action()
            _state.update { it.copy(loading = false) }
            result.onSuccess { onSuccess() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }
}
