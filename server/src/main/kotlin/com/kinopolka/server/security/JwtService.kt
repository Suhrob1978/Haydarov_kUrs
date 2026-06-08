package com.kinopolka.server.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${app.jwt.secret}") secret: String,
    @Value("\${app.jwt.expiration-ms}") private val expirationMs: Long,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secret))

    fun generateToken(userId: Long): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(Date(now.time + expirationMs))
            .signWith(key)
            .compact()
    }

    /** Возвращает id пользователя из токена либо null, если токен невалиден. */
    fun parseUserId(token: String): Long? = try {
        Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).payload.subject.toLong()
    } catch (e: Exception) {
        null
    }
}
