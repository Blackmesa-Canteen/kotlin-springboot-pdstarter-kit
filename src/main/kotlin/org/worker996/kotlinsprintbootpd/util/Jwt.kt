package org.worker996.kotlinsprintbootpd.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

/**
 * Jwt class provides methods for generating and decoding JWT tokens.
 *
 * @property secret the secret key used to sign and verify the tokens
 * @property expiration the duration after which the token will expire, default value is set to 30 days
 */
class Jwt(secret: String, private val expiration: Duration = 30.days) {

    private val algorithm = Algorithm.HMAC256(secret)
    private val verifier = JWT.require(algorithm).build()

    /**
     * Generates a JWT token for the given user ID.
     *
     * @param userId the ID of the user for whom the token is being generated
     * @param expiration the duration after which the token will expire,
     *        default value is set to the expiration duration specified in the Jwt class
     * @return a JWT token as a string
     */
    fun generateToken(userId: String, expiration: Duration = this.expiration): String =
        JWT.create()
            .withSubject(userId)
            .withExpiresAt(Instant.now() + expiration.toJavaDuration())
            .sign(algorithm)

    /**
     * Decodes a JWT token and returns the subject (user ID) embedded in the token.
     *
     * @param token the JWT token to decode
     * @return the subject (user ID) extracted from the token
     */
    fun decodeToken(token: String): String =
        verifier.verify(token).subject
}