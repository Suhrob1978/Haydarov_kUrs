package com.kinopolka.app.data.remote

import com.kinopolka.app.data.model.BacklogStatus
import com.kinopolka.app.data.model.UserDto

// ---- Auth ----
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class AuthResponse(
    val token: String,
    val user: UserDto,
)

// ---- Backlog ----
data class AddToBacklogRequest(
    val movieId: Long,
    val status: BacklogStatus = BacklogStatus.WANT,
)

/** Тело ошибки, возвращаемое сервером: {"error": "..."}. */
data class ErrorResponse(
    val error: String? = null,
)
