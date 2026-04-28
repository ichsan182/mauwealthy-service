package com.mauwealthy.web.service

import com.mauwealthy.web.dto.DebtSummaryPayload
import com.mauwealthy.web.entity.Debt

internal object DebtSummaryCalculator {
    fun fromDebts(debts: Iterable<Debt>): DebtSummaryPayload = DebtSummaryPayload(
        totalPrincipalAmount = debts.sumOf { it.principalAmount },
        totalRemainingAmount = debts.sumOf { it.remainingAmount },
    )
}

