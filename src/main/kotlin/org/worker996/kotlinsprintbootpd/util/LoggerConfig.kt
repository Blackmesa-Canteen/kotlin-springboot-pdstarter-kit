package org.worker996.kotlinsprintbootpd.util

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfig {

    @Bean
    fun logger() = LoggerFactory.getLogger("org.worker996.kotlinsprintbootpd")
}