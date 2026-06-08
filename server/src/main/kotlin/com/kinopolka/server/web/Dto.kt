package com.kinopolka.server.web

import com.kinopolka.server.domain.BacklogStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// ---- Auth ----
data class RegisterRequest(
    @field:Email val email: String,
    @field:Size(min = 6, message = "Пароль не короче 6 символов") val password: String,
    @field:NotBlank val displayName: String,
)

data class LoginRequest(
    @field:Email val email: String,
    @field:NotBlank val password: String,
)

data class AuthResponse(
    val token: String,
    val user: UserDto,
)

data class UserDto(
    val id: Long,
    val email: String,
    val displayName: String,
)

// ---- Movies ----
data class MovieDto(
    val id: Long,
    val title: String,
    val year: Int,
    val genre: String,
    val description: String,
    val posterUrl: String,
    val rating: Double,
)

// ---- Backlog ----
data class BacklogItemDto(
    val id: Long,
    val movie: MovieDto,
    val status: BacklogStatus,
    val userRating: Int,
    val note: String,
)

data class AddToBacklogRequest(
    val movieId: Long,
    val status: BacklogStatus = BacklogStatus.WANT,
)

data class UpdateBacklogRequest(
    val status: BacklogStatus?,
    @field:Min(0) @field:Max(10) val userRating: Int?,
    val note: String?,
)

data class BacklogStatsDto(
    val want: Long,
    val watching: Long,
    val watched: Long,
    val total: Long,
)
