package com.mauwealthy.web.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.cors")
data class CorsProperties(
    var allowedOriginPatterns: List<String> =
        listOf(
            "http://localhost:4200",
            "http://127.0.0.1:4200",
            "http://127.0.0.1:12653",
            "https://mauwealthy.my.id",
            "https://*.mauwealthy.my.id",
        ),
    var allowedMethods: List<String> = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"),
    var allowedHeaders: List<String> =
        listOf("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"),
    var exposedHeaders: List<String> = listOf("Authorization", "Content-Type", "Location"),
    var allowCredentials: Boolean = true,
    var maxAge: Long = 3600,
)

