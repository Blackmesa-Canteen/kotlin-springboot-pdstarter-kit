package org.worker996.kotlinsprintbootpd.web

import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.getBean
import org.springframework.http.HttpHeaders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.paramOrNull
import org.springframework.web.servlet.support.RequestContextUtils
import org.worker996.kotlinsprintbootpd.util.Jwt
import org.worker996.kotlinsprintbootpd.util.unauthorized

fun ServerRequest.context(): WebApplicationContext {
    return RequestContextUtils.findWebApplicationContext(this.servletRequest())!!
}

/**
 * Returns the authentication token extracted from the Authorization header of the ServerRequest.
 *
 * @return the authentication token as a string, or null if not found or invalid
 */
fun ServerRequest.token(): String? {
    return headers()
        .header(HttpHeaders.AUTHORIZATION)
        .getOrNull(0)
        ?.takeIf { it.startsWith("Token ", true) }
        ?.substring("Token ".length)
}

/**
 * Returns the user ID extracted from the JWT token in the Authorization header of the ServerRequest.
 *
 * @return the user ID as a string
 * @throws ResponseStatusException if the token is not found in the Authorization header or if the token is invalid
 */
fun ServerRequest.userId(): String {
    val token = token() ?: unauthorized("Token not found")
    val jwt = context().getBean<Jwt>()

    try {
        return jwt.decodeToken(token)
    } catch (e: JWTVerificationException) {
        unauthorized(e.message)
    }
}

fun ServerRequest.userIdOrNull(): String? {
    return runCatching { userId() }.getOrNull()
}

fun ServerRequest.pageParams(): PageParams {
    val offset = paramOrNull("offset")?.toLongOrNull() ?: 0
    val limit = paramOrNull("limit")?.toIntOrNull() ?: 20
    return PageParams(offset, limit)
}

data class PageParams(val offset: Long, val limit: Int)