package com.mauwealthy.web.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.cors.DefaultCorsProcessor
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

class SecurityConfigTests {

    private val securityConfig =
        SecurityConfig(
            CorsProperties(
                allowedOriginPatterns =
                    listOf(
                        "http://localhost:4200",
                        "http://127.0.0.1:4200",
                        "http://127.0.0.1:12653",
                        "https://mauwealthy.my.id",
                        "https://*.mauwealthy.my.id",
                    ),
            ),
        )

    @Test
    fun `allows preflight patch requests from production subdomains`() {
        val request = preflightRequest("https://app.mauwealthy.my.id", "PATCH")
        val response = MockHttpServletResponse()
        val config = corsConfiguration()

        val allowed = DefaultCorsProcessor().processRequest(config, request, response)

        assertTrue(allowed)
        assertEquals("https://app.mauwealthy.my.id", response.getHeader("Access-Control-Allow-Origin"))
        assertEquals("true", response.getHeader("Access-Control-Allow-Credentials"))
    }

    @Test
    fun `allows preflight post requests from localhost angular app`() {
        val request = preflightRequest("http://localhost:4200", "POST")
        val response = MockHttpServletResponse()
        val config = corsConfiguration()

        val allowed = DefaultCorsProcessor().processRequest(config, request, response)

        assertTrue(allowed)
        assertEquals("http://localhost:4200", response.getHeader("Access-Control-Allow-Origin"))
    }

    @Test
    fun `allows preflight patch requests from local backend origin when used in browser`() {
        val request = preflightRequest("http://127.0.0.1:12653", "PATCH")
        val response = MockHttpServletResponse()
        val config = corsConfiguration()

        val allowed = DefaultCorsProcessor().processRequest(config, request, response)

        assertTrue(allowed)
        assertEquals("http://127.0.0.1:12653", response.getHeader("Access-Control-Allow-Origin"))
    }

    @Test
    fun `rejects origins outside the configured allow list`() {
        val request = preflightRequest("https://evil.example.com", "PUT")
        val response = MockHttpServletResponse()
        val config = corsConfiguration()

        val allowed = DefaultCorsProcessor().processRequest(config, request, response)

        assertFalse(allowed)
    }

    private fun corsConfiguration() =
        (securityConfig.corsConfigurationSource() as UrlBasedCorsConfigurationSource)
            .getCorsConfiguration(MockHttpServletRequest("OPTIONS", "/api/users/test/financial-data"))!!

    private fun preflightRequest(origin: String, method: String) =
        MockHttpServletRequest("OPTIONS", "/api/users/test/financial-data").apply {
            addHeader("Origin", origin)
            addHeader("Access-Control-Request-Method", method)
            addHeader("Access-Control-Request-Headers", "content-type,authorization")
        }
}
