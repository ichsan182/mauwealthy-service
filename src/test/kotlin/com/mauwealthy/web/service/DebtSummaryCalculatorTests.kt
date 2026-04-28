package com.mauwealthy.web.service

import com.mauwealthy.web.entity.Debt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DebtSummaryCalculatorTests {

    @Test
    fun `returns zero totals when user has no debts`() {
        val summary = DebtSummaryCalculator.fromDebts(emptyList())

        assertEquals(0, summary.totalPrincipalAmount)
        assertEquals(0, summary.totalRemainingAmount)
    }

    @Test
    fun `aggregates principal and remaining totals across multiple debts`() {
        val debts = listOf(
            Debt(id = "debt-1", principalAmount = 1_000_000, remainingAmount = 500_000),
            Debt(id = "debt-2", principalAmount = 1_000_000, remainingAmount = 1_000_000),
            Debt(id = "debt-3", principalAmount = 1_000_000, remainingAmount = 1_000_000),
        )

        val summary = DebtSummaryCalculator.fromDebts(debts)

        assertEquals(3_000_000, summary.totalPrincipalAmount)
        assertEquals(2_500_000, summary.totalRemainingAmount)
    }
}

