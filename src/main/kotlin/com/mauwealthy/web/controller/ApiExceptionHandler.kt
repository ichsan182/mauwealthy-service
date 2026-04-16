package com.mauwealthy.web.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

data class ApiErrorResponse(
    val message: String,
    val details: Map<String, String> = emptyMap(),
)

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(ex: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val fieldErrors = ex.bindingResult
            .allErrors
            .filterIsInstance<FieldError>()
            .associate { it.field to (it.defaultMessage ?: "invalid") }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse(message = "Validation failed", details = fieldErrors))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(ex: ResponseStatusException): ResponseEntity<ApiErrorResponse> {
        val status = ex.statusCode
        return ResponseEntity
            .status(status)
            .body(ApiErrorResponse(message = ex.reason ?: "Request failed"))
    }
}

