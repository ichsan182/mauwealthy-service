package com.mauwealthy.web.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableConfigurationProperties(CorsProperties::class)
class SecurityConfig(
    private val corsProperties: CorsProperties,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOriginPatterns = corsProperties.allowedOriginPatterns.map(String::trim).filter(String::isNotEmpty)
        config.allowedMethods = corsProperties.allowedMethods
        config.allowedHeaders = corsProperties.allowedHeaders
        config.exposedHeaders = corsProperties.exposedHeaders
        config.allowCredentials = corsProperties.allowCredentials
        config.maxAge = corsProperties.maxAge

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}

