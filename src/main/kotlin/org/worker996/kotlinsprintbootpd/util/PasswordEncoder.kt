package org.worker996.kotlinsprintbootpd.util

import java.security.MessageDigest

object PasswordEncoder {

    /**
     * Encodes a raw password using SHA-256 algorithm.
     *
     * @param rawPassword the raw password to be encoded.
     * @return the encoded password as a string.
     */
    fun encode(rawPassword: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(rawPassword.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    /**
     * Checks if a raw password matches an encoded password.
     *
     * @param rawPassword the raw password to be checked.
     * @param encodedPassword the encoded password to be compared against.
     * @return true if the raw password matches the encoded password, false otherwise.
     */
    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return encode(rawPassword) == encodedPassword
    }
}