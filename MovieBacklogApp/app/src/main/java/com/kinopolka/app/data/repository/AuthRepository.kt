package com.kinopolka.app.data.repository

import com.kinopolka.app.data.local.SessionManager
import com.kinopolka.app.data.model.UserDto
import com.kinopolka.app.data.remote.ApiService
import com.kinopolka.app.data.remote.LoginRequest
import com.kinopolka.app.data.remote.RegisterRequest
import com.kinopolka.app.data.remote.apiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val session: SessionManager,
) {
    suspend fun login(email: String, password: String): Result<UserDto> = apiCall {
        val response = api.login(LoginRequest(email = email.trim().lowercase(), password = password))
        session.save(response.token, response.user)
        response.user
    }

    suspend fun register(email: String, password: String, name: String): Result<UserDto> = apiCall {
        val response = api.register(
            RegisterRequest(
                email = email.trim().lowercase(),
                password = password,
                displayName = name.trim(),
            )
        )
        session.save(response.token, response.user)
        response.user
    }

    /** Данные текущего пользователя из локальной сессии. */
    fun currentUser(): UserDto? = session.currentUser()

    fun logout() = session.clear()
}
