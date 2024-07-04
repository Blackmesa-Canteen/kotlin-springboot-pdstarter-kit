package org.worker996.kotlinsprintbootpd.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.worker996.kotlinsprintbootpd.util.Jwt

@Configuration(proxyBeanMethods = false)
class WebConfiguration {

    /**
     * Provides a CorsFilter bean for handling Cross-Origin Resource Sharing (CORS) in the web application.
     *
     * This method creates a CorsConfiguration object with default settings:
     * - Allowed Origin Patterns: All origins are allowed.
     * - Allowed Methods: All HTTP methods are allowed.
     * - Allowed Headers: All headers are allowed.
     * - Allow Credentials: Cross-site requests can include credentials.
     */
    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf(CorsConfiguration.ALL)
            allowedMethods = listOf(CorsConfiguration.ALL)
            allowedHeaders = listOf(CorsConfiguration.ALL)
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }

        return CorsFilter(source)
    }

    /**
     *
     * Jwt Method
     *
     * This method creates an instance of the Jwt class by accepting the secret key as a parameter.
     *
     * @param secret the secret key used to sign and verify the tokens
     * @return an instance of the Jwt class
     */
    @Bean
    fun jwt(@Value("\${pd.jwt.secret}") secret: String) = Jwt(secret)
}