package com.accountbalanceservice.exception

import com.accountbalanceservice.model.WebResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): ResponseEntity<Any> {
        val errorResponse = WebResponse(
                code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                status = HttpStatus.INTERNAL_SERVER_ERROR.name,
                data = "Internal Server Error"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoResourceFoundException(ex: NoResourceFoundException): ResponseEntity<Any> {
        val errorResponse = WebResponse(
                code = HttpStatus.NOT_FOUND.value(),
                status = HttpStatus.NOT_FOUND.name,
                data = ex.message
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingPathVariable(ex: MissingServletRequestParameterException): ResponseEntity<Any> {
        val errorResponse = WebResponse(
                code = HttpStatus.BAD_REQUEST.value(),
                status = HttpStatus.BAD_REQUEST.name,
                data = "Missing path variable: ${ex.parameterName}"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchExceptions(ex: MethodArgumentTypeMismatchException): ResponseEntity<Any> {
        val errorResponse = WebResponse(
                code = HttpStatus.BAD_REQUEST.value(),
                status = HttpStatus.BAD_REQUEST.name,
                data = ex.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Validation failed"
        }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<Any> {
        val errorResponse = WebResponse(
                code = HttpStatus.NOT_FOUND.value(),
                status = HttpStatus.NOT_FOUND.name,
                data = ex.message
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

}