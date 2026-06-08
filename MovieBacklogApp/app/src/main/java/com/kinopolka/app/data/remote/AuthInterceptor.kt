package com.kinopolka.app.data.remote

import com.kinopolka.app.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Подставляет JWT-токен в заголовок Authorization для всех запросов, если
 * пользователь авторизован. Открытые эндпоинты (login/register) на сервере
 * этот заголовок просто игнорируют.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val session: SessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = session.token()
        val request = chain.request()
        val authorized = if (token.isNullOrBlank()) {
            request
        } else {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(authorized)
    }
}
