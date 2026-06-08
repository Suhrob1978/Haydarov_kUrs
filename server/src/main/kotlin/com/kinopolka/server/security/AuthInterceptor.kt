package com.kinopolka.server.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * Проверяет JWT в заголовке Authorization для защищённых эндпоинтов и кладёт
 * id пользователя в атрибут запроса "userId". Открытые пути (эндпоинты auth,
 * консоль H2) пропускаются на уровне конфигурации.
 */
@Component
class AuthInterceptor(private val jwtService: JwtService) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val header = request.getHeader("Authorization")
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Требуется авторизация")
            return false
        }
        val userId = jwtService.parseUserId(header.removePrefix("Bearer ").trim())
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Недействительный токен")
            return false
        }
        request.setAttribute("userId", userId)
        return true
    }
}
