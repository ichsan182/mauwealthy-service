package com.mauwealthy.web.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UserPayload(
    @field:NotBlank
    val id: String,

    @field:NotBlank
    val name: String,

    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val phone: String,

    @field:NotBlank
    val password: String,

    @field:NotNull
    val onboardingCompleted: Boolean,

    @field:NotNull
    val level: Int,

    @field:Valid
    val investmentWatchlist: InvestmentWatchlistPayload?,

    @field:Valid
    val journal: JournalPayload?,

    @field:Valid
    val financialData: FinancialDataPayload?,

    @field:Valid
    val streak: StreakPayload?,

    @field:Valid
    val debts: List<DebtPayload> = emptyList(),
)

data class InvestmentWatchlistPayload(
    val items: List<WatchlistItemPayload> = emptyList(),
    val selectedSymbol: String? = null,
    val updatedAt: String? = null,
)

data class WatchlistItemPayload(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val currency: String,
    val createdAt: String? = null,
)

data class JournalPayload(
    val nextChatMessageId: Int,
    val chatByDate: Map<String, List<ChatMessagePayload>> = emptyMap(),
    val expensesByDate: Map<String, List<ExpensePayload>> = emptyMap(),
    val incomesByDate: Map<String, List<IncomePayload>> = emptyMap(),
)

data class ChatMessagePayload(
    val id: Int,
    val sender: String,
    val text: String,
    val time: String,
    val parsedText: String? = null,
    val parsedNominal: Long? = null,
)

data class ExpensePayload(
    val amount: Long,
    val description: String,
    val category: String,
)

data class IncomePayload(
    val amount: Long,
    val description: String,
    val source: String,
)

data class FinancialDataPayload(
    val pendapatan: Long,
    val pengeluaranWajib: Long,
    val tanggalPemasukan: Int,
    val intendedTanggalPemasukan: Int,
    val hutangWajib: Long,
    val estimasiTabungan: Long,
    val danaDarurat: Long,
    val danaInvestasi: Long = 0,
    val budgetAllocation: BudgetAllocationPayload,
    val currentPengeluaranLimit: Long,
    val currentPengeluaranUsed: Long,
    val currentSisaSaldoPool: Long,
    val lastCycleCarryOverSaldo: Long,
    val monthlyTopUp: MonthlyTopUpPayload,
    val currentCycleStart: String?,
    val currentCycleEnd: String?,
    val savingsAllocation: SavingsAllocationPayload = SavingsAllocationPayload(),
    val investmentTracking: InvestmentTrackingPayload = InvestmentTrackingPayload(),
)

data class FinancialDataPatchPayload(
    val pendapatan: Long? = null,
    val pengeluaranWajib: Long? = null,
    val tanggalPemasukan: Int? = null,
    val intendedTanggalPemasukan: Int? = null,
    val hutangWajib: Long? = null,
    val estimasiTabungan: Long? = null,
    val danaDarurat: Long? = null,
    val danaInvestasi: Long? = null,
    val budgetAllocation: BudgetAllocationPatchPayload? = null,
    val currentPengeluaranLimit: Long? = null,
    val currentPengeluaranUsed: Long? = null,
    val currentSisaSaldoPool: Long? = null,
    val lastCycleCarryOverSaldo: Long? = null,
    val monthlyTopUp: MonthlyTopUpPatchPayload? = null,
    val currentCycleStart: String? = null,
    val currentCycleEnd: String? = null,
    val savingsAllocation: SavingsAllocationPatchPayload? = null,
    val savingsAllocationDelta: SavingsAllocationPatchPayload? = null,
    val investmentTracking: InvestmentTrackingPatchPayload? = null,
)

data class SavingsAllocationPayload(
    val tabungan: Long = 0,
    val danaDarurat: Long = 0,
    val danaInvestasi: Long = 0,
)

data class SavingsAllocationPatchPayload(
    val tabungan: Long? = null,
    val danaDarurat: Long? = null,
    val danaInvestasi: Long? = null,
)

data class InvestmentTrackingPayload(
    val cycleAmounts: Map<String, Long> = emptyMap(),
)

data class InvestmentTrackingPatchPayload(
    val cycleAmounts: Map<String, Long>? = null,
)

data class BudgetAllocationPayload(
    val mode: Int,
    val pengeluaran: Int,
    val wants: Int,
    val savings: Int,
)

data class BudgetAllocationPatchPayload(
    val mode: Int? = null,
    val pengeluaran: Int? = null,
    val wants: Int? = null,
    val savings: Int? = null,
)

data class MonthlyTopUpPayload(
    val cycleKey: String?,
    val fromTabunganCount: Int,
    val totalFromTabungan: Long,
    val totalFromDanaDarurat: Long,
)

data class MonthlyTopUpPatchPayload(
    val cycleKey: String? = null,
    val fromTabunganCount: Int? = null,
    val totalFromTabungan: Long? = null,
    val totalFromDanaDarurat: Long? = null,
)

data class StreakPayload(
    val current: Int,
    val longest: Int,
    val lastActiveDate: String?,
    val freezeUsed: Boolean,
)

data class DebtPayload(
    val id: String,
    val name: String,
    val category: String,
    val debtType: String,
    val principalAmount: Long,
    val remainingAmount: Long,
    val monthlyInstallment: Long,
    val dueDay: Int,
    val notes: String,
)

data class InvestmentWatchlistPatchPayload(
    val items: List<WatchlistItemPayload>? = null,
    val selectedSymbol: String? = null,
    val updatedAt: String? = null,
)

data class CreateChatMessageRequest(
    val sender: String,
    val text: String,
    val time: String,
)

data class CreateExpenseRequest(
    val amount: Long,
    val description: String,
    val category: String,
)

data class CreateIncomeRequest(
    val amount: Long,
    val description: String,
    val source: String,
)

