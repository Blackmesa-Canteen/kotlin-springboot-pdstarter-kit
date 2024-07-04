package org.worker996.kotlinsprintbootpd.util

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


/**
 * throw a unauthorized exception
 */
fun unauthorized(message: String? = null): Nothing =
    throw ResponseStatusException(HttpStatus.UNAUTHORIZED, message)

/**
 * throw a forbidden exception
 */
fun forbidden(message: String? = null): Nothing =
    throw ResponseStatusException(HttpStatus.FORBIDDEN, message)

/**
 * throw a not found exception
 */
fun notFound(message: String? = null): Nothing =
    throw ResponseStatusException(HttpStatus.NOT_FOUND, message)

/**
 * throw unprocessable exception
 */
fun unprocessable(message: String? = null): Nothing =
    throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, message)

fun DataIntegrityViolationException.isUniqueConstraintException(constraintName: String): Boolean {
    // Example logic: this will depend on your specific SQL dialect and driver
    return this.message?.contains(constraintName) == true
}