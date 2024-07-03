package org.worker996.kotlinsprintbootpd.util

import com.github.slugify.Slugify
import java.util.*

val SLUGIFY: Slugify = Slugify.builder().build()

/**
 * Converts a string into a slug format.
 *
 * @param randomSuffix (optional) Determines whether to add a random suffix to the slug. Defaults to false.
 * @return The slugified version of the string.
 */
fun String.slugify(randomSuffix: Boolean = false): String {
    val slug = SLUGIFY.slugify(this)
    return if (randomSuffix) "$slug-${randomKey()}" else slug
}

private fun randomKey(): String = UUID.randomUUID().toString().substring(0, 8)