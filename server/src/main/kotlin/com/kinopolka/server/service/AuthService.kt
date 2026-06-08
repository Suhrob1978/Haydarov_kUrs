package com.kinopolka.server.service

import com.kinopolka.server.domain.User
import com.kinopolka.server.domain.UserRepository
import com.kinopolka.server.security.JwtService
import com.kinopolka.server.web.*
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val users: UserRepository,
    private val jwtService: JwtService,
) {
    private val encoder = BCryptPasswordEncoder()

    fun register(req: RegisterRequest): AuthResponse {
        if (users.existsByEmail(req.email.lowercase())) {
            throw ApiException(HttpStatus.CONFLICT, "Пользователь с такой почтой уже существует")
        }
        val user = users.save(
            User(
                email = req.email.lowercase(),
                passwordHash = encoder.encode(req.password),
                displayName = req.displayName.trim(),
            )
        )
        return AuthResponse(jwtService.generateToken(user.id), user.toDto())
    }

    fun login(req: LoginRequest): AuthResponse {
        val user = users.findByEmail(req.email.lowercase())
            ?: throw ApiException(HttpStatus.UNAUTHORIZED, "Неверная почта или пароль")
        if (!encoder.matches(req.password, user.passwordHash)) {
            throw ApiException(HttpStatus.UNAUTHORIZED, "Неверная почта или пароль")
        }
        return AuthResponse(jwtService.generateToken(user.id), user.toDto())
    }

    fun currentUser(userId: Long): UserDto {
        val user = users.findById(userId).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "Пользователь не найден")
        }
        return user.toDto()
    }
}
