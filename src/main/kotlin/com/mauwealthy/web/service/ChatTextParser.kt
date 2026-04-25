package com.mauwealthy.web.service

/**
 * Parses free-form chat text from user into (parsedText, parsedNominal).
 *
 * Supported patterns:
 *   "makan 10000"               → text="makan",           nominal=10000
 *   "10000 makan"               → text="makan",           nominal=10000
 *   "makan sapi 10000"          → text="makan sapi",      nominal=10000
 *   "123456789 netflix nonton"  → text="netflix nonton",  nominal=123456789
 *   "pergi 12345690"            → text="pergi",           nominal=12345690
 *   "halo saja"                 → text="halo saja",       nominal=null
 *   "100000"                    → text=null,              nominal=100000
 *
 * Rules:
 *  - A token is a "nominal" if it consists entirely of digits (no letters).
 *  - Only the FIRST pure-digit token is treated as nominal; rest is text.
 *  - Both parsedText and parsedNominal can be null.
 */
object ChatTextParser {

    private val PURE_DIGITS = Regex("""^\d+$""")

    data class ParseResult(
        val parsedText: String?,
        val parsedNominal: Long?,
    )

    fun parse(input: String): ParseResult {
        val tokens = input.trim().split(Regex("""\s+"""))
        var nominal: Long? = null
        val textTokens = mutableListOf<String>()

        for (token in tokens) {
            if (nominal == null && PURE_DIGITS.matches(token)) {
                // Take the first pure-digit token as nominal
                nominal = token.toLongOrNull()
            } else {
                textTokens.add(token)
            }
        }

        val text = textTokens.joinToString(" ").trim().ifBlank { null }
        return ParseResult(parsedText = text, parsedNominal = nominal)
    }
}

