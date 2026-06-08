package com.kinopolka.server.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/** Доменная ошибка с конкретным HTTP-статусом. */
class ApiException(val status: HttpStatus, override val message: String) : RuntimeException(message)

data class ErrorResponse(val error: String)

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(ApiException::class)
    fun handleApi(ex: ApiException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(ex.status).body(ErrorResponse(ex.message))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val msg = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Некорректные данные"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(msg))
    }
}
