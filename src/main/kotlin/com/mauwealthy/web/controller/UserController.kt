package com.mauwealthy.web.controller

import com.mauwealthy.web.dto.ChatMessagePayload
import com.mauwealthy.web.dto.CreateChatMessageRequest
import com.mauwealthy.web.dto.CreateExpenseRequest
import com.mauwealthy.web.dto.CreateIncomeRequest
import com.mauwealthy.web.dto.DebtPayload
import com.mauwealthy.web.dto.ExpensePayload
import com.mauwealthy.web.dto.FinancialDataPatchPayload
import com.mauwealthy.web.dto.IncomePayload
import com.mauwealthy.web.dto.InvestmentWatchlistPatchPayload
import com.mauwealthy.web.dto.UserPayload
import com.mauwealthy.web.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    /** POST /api/users - Create a full user profile from db.json-style payload. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody payload: UserPayload): UserPayload = userService.create(payload)

    /** GET /api/users - Get all users including nested profile data. */
    @GetMapping
    fun findAll(): List<UserPayload> = userService.findAll()

    /** GET /api/users/{id} - Get one user profile by id. */
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): UserPayload = userService.findById(id)

    /** PUT /api/users/{id} - Replace one user profile with full payload. */
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody payload: UserPayload,
    ): UserPayload = userService.update(id, payload)

    /** PATCH /api/users/{id}/financial-data - Update only selected financialData fields. */
    @PatchMapping("/{id}/financial-data")
    fun patchFinancialData(
        @PathVariable id: String,
        @RequestBody payload: FinancialDataPatchPayload,
    ): UserPayload = userService.patchFinancialData(id, payload)

    /** PATCH /api/users/{id}/investment-watchlist - Partial update watchlist (items and/or selectedSymbol). */
    @PatchMapping("/{id}/investment-watchlist")
    fun patchInvestmentWatchlist(
        @PathVariable id: String,
        @RequestBody payload: InvestmentWatchlistPatchPayload,
    ): UserPayload = userService.patchWatchlist(id, payload)

    /** DELETE /api/users/{id} - Remove a user and all related nested data. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) = userService.delete(id)

    /** GET /api/users/{id}/debts - Get all debt records for a specific user. */
    @GetMapping("/{id}/debts")
    fun findDebts(@PathVariable id: String): List<DebtPayload> = userService.findDebtsByUserId(id)

    /** POST /api/users/{id}/debts - Add one debt item to a user. */
    @PostMapping("/{id}/debts")
    @ResponseStatus(HttpStatus.CREATED)
    fun addDebt(
        @PathVariable id: String,
        @Valid @RequestBody payload: DebtPayload,
    ): DebtPayload = userService.addDebt(id, payload)

    /** GET /api/users/{id}/journal/chats?date=yyyy-MM-dd - Get chat messages by date. */
    @GetMapping("/{id}/journal/chats")
    fun findChatByDate(
        @PathVariable id: String,
        @RequestParam date: String,
    ): List<ChatMessagePayload> = userService.findChatByDate(id, date)

    /** POST /api/users/{id}/journal/chats?date=yyyy-MM-dd - Add a chat message for one date. */
    @PostMapping("/{id}/journal/chats")
    @ResponseStatus(HttpStatus.CREATED)
    fun addChatMessage(
        @PathVariable id: String,
        @RequestParam date: String,
        @RequestBody payload: CreateChatMessageRequest,
    ): ChatMessagePayload = userService.addChatMessage(id, date, payload)

    /** GET /api/users/{id}/journal/expenses?date=yyyy-MM-dd - Get expenses by date. */
    @GetMapping("/{id}/journal/expenses")
    fun findExpensesByDate(
        @PathVariable id: String,
        @RequestParam date: String,
    ): List<ExpensePayload> = userService.findExpensesByDate(id, date)

    /** POST /api/users/{id}/journal/expenses?date=yyyy-MM-dd - Add an expense for one date. */
    @PostMapping("/{id}/journal/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    fun addExpense(
        @PathVariable id: String,
        @RequestParam date: String,
        @RequestBody payload: CreateExpenseRequest,
    ): ExpensePayload = userService.addExpense(id, date, payload)

    /** GET /api/users/{id}/journal/incomes?date=yyyy-MM-dd - Get incomes by date. */
    @GetMapping("/{id}/journal/incomes")
    fun findIncomesByDate(
        @PathVariable id: String,
        @RequestParam date: String,
    ): List<IncomePayload> = userService.findIncomesByDate(id, date)

    /** POST /api/users/{id}/journal/incomes?date=yyyy-MM-dd - Add an income for one date. */
    @PostMapping("/{id}/journal/incomes")
    @ResponseStatus(HttpStatus.CREATED)
    fun addIncome(
        @PathVariable id: String,
        @RequestParam date: String,
        @RequestBody payload: CreateIncomeRequest,
    ): IncomePayload = userService.addIncome(id, date, payload)
}