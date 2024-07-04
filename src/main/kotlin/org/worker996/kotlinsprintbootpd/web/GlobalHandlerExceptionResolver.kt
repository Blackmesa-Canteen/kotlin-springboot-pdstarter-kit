package org.worker996.kotlinsprintbootpd.web

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
import java.lang.Exception

/**
 * An implementation of [AbstractHandlerExceptionResolver] that handles global exception handling.
 *
 * This resolver is responsible for mapping exceptions to appropriate error responses. It utilizes
 * an instance of [ObjectMapper] to convert exception objects to JSON representations.
 *
 * @property mapper The [ObjectMapper] instance used for exception JSON serialization.
 */
@Component
class GlobalHandlerExceptionResolver(private val mapper: ObjectMapper): AbstractHandlerExceptionResolver() {

    init {
        order = Ordered.HIGHEST_PRECEDENCE
    }

    override fun doResolveException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any?,
        ex: Exception
    ): ModelAndView? {
        val (status, error) = statusAndError(ex)
        if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            logUnknownException(request, ex)
        }

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = status
        response.writer.write(mapper.writeValueAsString(error))
        return ModelAndView()
    }

    /**
     * Returns the status code and error response for the given exception.
     *
     * @param ex The exception for which to retrieve the status code and error response.
     * @return A Pair containing the status code and error response.
     */
    private fun statusAndError(ex: Exception): Pair<Int, ErrorResponse> {
        return when (ex) {
            is HttpMediaTypeNotSupportedException -> HttpStatus.UNSUPPORTED_MEDIA_TYPE.value() to ErrorResponse.of(ex)
            is HttpMediaTypeNotAcceptableException -> HttpStatus.NOT_ACCEPTABLE.value() to ErrorResponse.of(ex)
            is HttpMessageNotReadableException -> HttpStatus.UNSUPPORTED_MEDIA_TYPE.value() to ErrorResponse.of(ex)
            is NoSuchElementException -> HttpStatus.NOT_FOUND.value() to ErrorResponse.of(ex)
            is EntityNotFoundException -> HttpStatus.NOT_FOUND.value() to ErrorResponse.of(ex)
            is ResponseStatusException -> ex.statusCode.value() to ErrorResponse.of(ex.reason)
            else -> HttpStatus.INTERNAL_SERVER_ERROR.value() to ErrorResponse.of("UNKNOWN_ERROR")
        }
    }

    /**
     * Logs an unknown exception.
     *
     * @param request The HttpServletRequest object representing the request.
     * @param ex The Exception object representing the unknown exception.
     */
    private fun logUnknownException(request: HttpServletRequest, ex: Exception) {
        logger.error("Unknown error on ${request.method} ${request.requestURI}", ex)
    }

    /**
     * Data class representing an error response.
     *
     * @property body The body of the error response.
     *
     * @constructor Creates an ErrorResponse with the given body.
     */
    private data class ErrorResponse(val body: Body) {
        data class Body(val errors: List<String>)

        companion object {
            fun of(messages: List<String>) = ErrorResponse(Body(messages))

            fun of(message: String?) = if (message.isNullOrEmpty()) of(listOf()) else of(listOf(message))

            fun of(ex: Exception) = of(ex.message)
        }
    }
}