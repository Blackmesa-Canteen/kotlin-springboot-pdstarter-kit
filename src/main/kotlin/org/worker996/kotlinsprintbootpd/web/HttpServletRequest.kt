package org.worker996.kotlinsprintbootpd.web

import com.auth0.jwt.exceptions.JWTVerificationException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.support.RequestContextUtils
import org.worker996.kotlinsprintbootpd.util.Jwt
import org.springframework.http.HttpHeaders
import org.worker996.kotlinsprintbootpd.util.unauthorized

fun HttpServletRequest.context(): WebApplicationContext {
    return RequestContextUtils.findWebApplicationContext(this)!!
}

fun HttpServletRequest.token(): String? {
    return this.getHeader(HttpHeaders.AUTHORIZATION)
        ?.takeIf { it.startsWith("Token ", true) }
        ?.substring("Token ".length)
}

fun HttpServletRequest.userId(): String {
    val token = token() ?: unauthorized("Token not found")
    val jwt = context().getBean<Jwt>()

    try {
        return jwt.decodeToken(token)
    } catch (e: JWTVerificationException) {
        unauthorized(e.message)
    }
}

fun HttpServletRequest.userIdOrNull(): String? {
    return runCatching { userId() }.getOrNull()
}

fun HttpServletRequest.pageParams(): PageParams {
    val offset = this.getParameter("offset")?.toLongOrNull() ?: 0
    val limit = this.getParameter("limit")?.toIntOrNull() ?: 20
    return PageParams(offset, limit)
}

data class PageParams(val offset: Long, val limit: Int)