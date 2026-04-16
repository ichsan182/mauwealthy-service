package com.mauwealthy.web.service

import com.mauwealthy.web.dto.ChatMessagePayload
import com.mauwealthy.web.dto.CreateChatMessageRequest
import com.mauwealthy.web.dto.DebtPayload
import com.mauwealthy.web.dto.UserPayload
import com.mauwealthy.web.entity.BudgetAllocation
import com.mauwealthy.web.entity.ChatMessage
import com.mauwealthy.web.entity.Debt
import com.mauwealthy.web.entity.ExpenseEntry
import com.mauwealthy.web.entity.FinancialData
import com.mauwealthy.web.entity.IncomeEntry
import com.mauwealthy.web.entity.InvestmentWatchlist
import com.mauwealthy.web.entity.Journal
import com.mauwealthy.web.entity.MonthlyTopUp
import com.mauwealthy.web.entity.Streak
import com.mauwealthy.web.entity.User
import com.mauwealthy.web.entity.WatchlistItem
import com.mauwealthy.web.repository.DebtRepository
import com.mauwealthy.web.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDate

@Service
class UserService(
    private val userRepository: UserRepository,
    private val debtRepository: DebtRepository,
) {
    fun findAll(): List<UserPayload> = userRepository.findAll().map(::toPayload)

    fun findById(id: String): UserPayload = toPayload(getUserOrThrow(id))

    @Transactional
    fun create(payload: UserPayload): UserPayload {
        if (userRepository.existsById(payload.id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User id already exists")
        }
        if (userRepository.existsByEmail(payload.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already exists")
        }

        val saved = userRepository.save(toEntity(payload, null))
        return toPayload(saved)
    }

    @Transactional
    fun update(id: String, payload: UserPayload): UserPayload {
        if (id != payload.id) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Path id and body id must match")
        }

        val existing = getUserOrThrow(id)
        if (existing.email != payload.email && userRepository.existsByEmail(payload.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already exists")
        }

        val saved = userRepository.save(toEntity(payload, existing))
        return toPayload(saved)
    }

    @Transactional
    fun delete(id: String) {
        if (!userRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }
        userRepository.deleteById(id)
    }

    fun findDebtsByUserId(userId: String): List<DebtPayload> {
        if (!userRepository.existsById(userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }
        return debtRepository.findAllByUserId(userId).map(::toDebtPayload)
    }

    @Transactional
    fun addDebt(userId: String, debtPayload: DebtPayload): DebtPayload {
        val user = getUserOrThrow(userId)
        val debt = Debt(
            id = debtPayload.id,
            name = debtPayload.name,
            category = debtPayload.category,
            debtType = debtPayload.debtType,
            principalAmount = debtPayload.principalAmount,
            remainingAmount = debtPayload.remainingAmount,
            monthlyInstallment = debtPayload.monthlyInstallment,
            dueDay = debtPayload.dueDay,
            notes = debtPayload.notes,
        )
        debt.user = user
        return toDebtPayload(debtRepository.save(debt))
    }

    fun findChatByDate(userId: String, date: String): List<ChatMessagePayload> {
        val parsedDate = parseDateOrThrow(date)
        val journal = getUserOrThrow(userId).journal
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Journal not found")

        return journal.chatMessages
            .asSequence()
            .filter { it.chatDate == parsedDate }
            .sortedBy { it.messageId }
            .map {
                ChatMessagePayload(
                    id = it.messageId,
                    sender = it.sender,
                    text = it.text,
                    time = it.time,
                )
            }
            .toList()
    }

    @Transactional
    fun addChatMessage(userId: String, date: String, request: CreateChatMessageRequest): ChatMessagePayload {
        val parsedDate = parseDateOrThrow(date)
        val user = getUserOrThrow(userId)
        val journal = user.journal ?: Journal().also {
            it.user = user
            user.journal = it
        }

        val nextId = journal.nextChatMessageId
        val chat = ChatMessage(
            messageId = nextId,
            sender = request.sender,
            text = request.text,
            time = request.time,
            chatDate = parsedDate,
        )
        chat.journal = journal

        journal.chatMessages.add(chat)
        journal.nextChatMessageId = nextId + 1
        userRepository.save(user)

        return ChatMessagePayload(
            id = chat.messageId,
            sender = chat.sender,
            text = chat.text,
            time = chat.time,
        )
    }

    private fun getUserOrThrow(id: String): User = userRepository.findById(id)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }

    private fun toEntity(payload: UserPayload, existing: User?): User {
        val user = existing ?: User()
        user.id = payload.id
        user.name = payload.name
        user.email = payload.email
        user.phone = payload.phone
        user.password = payload.password
        user.onboardingCompleted = payload.onboardingCompleted
        user.level = payload.level

        user.investmentWatchlist = payload.investmentWatchlist?.let { watchlistPayload ->
            InvestmentWatchlist(
                selectedSymbol = watchlistPayload.selectedSymbol,
                updatedAt = parseInstantOrNull(watchlistPayload.updatedAt),
            ).also { watchlist ->
                watchlist.user = user
                watchlistPayload.items.forEach { itemPayload ->
                    val item = WatchlistItem(
                        symbol = itemPayload.symbol,
                        name = itemPayload.name,
                        type = itemPayload.type,
                        region = itemPayload.region,
                        currency = itemPayload.currency,
                        createdAt = parseInstantOrNull(itemPayload.createdAt),
                    )
                    item.watchlist = watchlist
                    watchlist.items.add(item)
                }
            }
        }

        user.journal = payload.journal?.let { journalPayload ->
            Journal(nextChatMessageId = journalPayload.nextChatMessageId).also { journal ->
                journal.user = user

                journalPayload.chatByDate.forEach { (date, messages) ->
                    val parsedDate = parseDateOrThrow(date)
                    messages.forEach { message ->
                        val chat = ChatMessage(
                            messageId = message.id,
                            sender = message.sender,
                            text = message.text,
                            time = message.time,
                            chatDate = parsedDate,
                        )
                        chat.journal = journal
                        journal.chatMessages.add(chat)
                    }
                }

                journalPayload.expensesByDate.forEach { (date, expenses) ->
                    val parsedDate = parseDateOrThrow(date)
                    expenses.forEach { expensePayload ->
                        val expense = ExpenseEntry(
                            amount = expensePayload.amount,
                            description = expensePayload.description,
                            category = expensePayload.category,
                            expenseDate = parsedDate,
                        )
                        expense.journal = journal
                        journal.expenses.add(expense)
                    }
                }

                journalPayload.incomesByDate.forEach { (date, incomes) ->
                    val parsedDate = parseDateOrThrow(date)
                    incomes.forEach { incomePayload ->
                        val income = IncomeEntry(
                            amount = incomePayload.amount,
                            description = incomePayload.description,
                            source = incomePayload.source,
                            incomeDate = parsedDate,
                        )
                        income.journal = journal
                        journal.incomes.add(income)
                    }
                }
            }
        }

        user.financialData = payload.financialData?.let { financialPayload ->
            FinancialData(
                pendapatan = financialPayload.pendapatan,
                pengeluaranWajib = financialPayload.pengeluaranWajib,
                tanggalPemasukan = financialPayload.tanggalPemasukan,
                intendedTanggalPemasukan = financialPayload.intendedTanggalPemasukan,
                hutangWajib = financialPayload.hutangWajib,
                estimasiTabungan = financialPayload.estimasiTabungan,
                danaDarurat = financialPayload.danaDarurat,
                currentPengeluaranLimit = financialPayload.currentPengeluaranLimit,
                currentPengeluaranUsed = financialPayload.currentPengeluaranUsed,
                currentSisaSaldoPool = financialPayload.currentSisaSaldoPool,
                lastCycleCarryOverSaldo = financialPayload.lastCycleCarryOverSaldo,
                currentCycleStart = parseLocalDateOrNull(financialPayload.currentCycleStart),
                currentCycleEnd = parseLocalDateOrNull(financialPayload.currentCycleEnd),
            ).also { financialData ->
                financialData.budgetAllocation = BudgetAllocation(
                    mode = financialPayload.budgetAllocation.mode,
                    pengeluaran = financialPayload.budgetAllocation.pengeluaran,
                    wants = financialPayload.budgetAllocation.wants,
                    savings = financialPayload.budgetAllocation.savings,
                )
                financialData.monthlyTopUp = MonthlyTopUp(
                    cycleKey = financialPayload.monthlyTopUp.cycleKey,
                    fromTabunganCount = financialPayload.monthlyTopUp.fromTabunganCount,
                    totalFromTabungan = financialPayload.monthlyTopUp.totalFromTabungan,
                    totalFromDanaDarurat = financialPayload.monthlyTopUp.totalFromDanaDarurat,
                )
                financialData.user = user
            }
        }

        user.streak = payload.streak?.let { streakPayload ->
            Streak(
                current = streakPayload.current,
                longest = streakPayload.longest,
                lastActiveDate = parseLocalDateOrNull(streakPayload.lastActiveDate),
                freezeUsed = streakPayload.freezeUsed,
            ).also { streak ->
                streak.user = user
            }
        }

        user.debts.clear()
        payload.debts.forEach { debtPayload ->
            val debt = Debt(
                id = debtPayload.id,
                name = debtPayload.name,
                category = debtPayload.category,
                debtType = debtPayload.debtType,
                principalAmount = debtPayload.principalAmount,
                remainingAmount = debtPayload.remainingAmount,
                monthlyInstallment = debtPayload.monthlyInstallment,
                dueDay = debtPayload.dueDay,
                notes = debtPayload.notes,
            )
            debt.user = user
            user.debts.add(debt)
        }

        return user
    }

    private fun toPayload(entity: User): UserPayload = UserPayload(
        id = entity.id,
        name = entity.name,
        email = entity.email,
        phone = entity.phone,
        password = entity.password,
        onboardingCompleted = entity.onboardingCompleted,
        level = entity.level,
        investmentWatchlist = entity.investmentWatchlist?.let { watchlist ->
            com.mauwealthy.web.dto.InvestmentWatchlistPayload(
                items = watchlist.items.map {
                    com.mauwealthy.web.dto.WatchlistItemPayload(
                        symbol = it.symbol,
                        name = it.name,
                        type = it.type,
                        region = it.region,
                        currency = it.currency,
                        createdAt = it.createdAt?.toString(),
                    )
                },
                selectedSymbol = watchlist.selectedSymbol,
                updatedAt = watchlist.updatedAt?.toString(),
            )
        },
        journal = entity.journal?.let { journal ->
            com.mauwealthy.web.dto.JournalPayload(
                nextChatMessageId = journal.nextChatMessageId,
                chatByDate = journal.chatMessages
                    .sortedBy { it.messageId }
                    .groupBy(
                        keySelector = { it.chatDate.toString() },
                        valueTransform = {
                            ChatMessagePayload(
                                id = it.messageId,
                                sender = it.sender,
                                text = it.text,
                                time = it.time,
                            )
                        },
                    ),
                expensesByDate = journal.expenses
                    .groupBy(
                        keySelector = { it.expenseDate.toString() },
                        valueTransform = {
                            com.mauwealthy.web.dto.ExpensePayload(
                                amount = it.amount,
                                description = it.description,
                                category = it.category,
                            )
                        },
                    ),
                incomesByDate = journal.incomes
                    .groupBy(
                        keySelector = { it.incomeDate.toString() },
                        valueTransform = {
                            com.mauwealthy.web.dto.IncomePayload(
                                amount = it.amount,
                                description = it.description,
                                source = it.source,
                            )
                        },
                    ),
            )
        },
        financialData = entity.financialData?.let { financial ->
            com.mauwealthy.web.dto.FinancialDataPayload(
                pendapatan = financial.pendapatan,
                pengeluaranWajib = financial.pengeluaranWajib,
                tanggalPemasukan = financial.tanggalPemasukan,
                intendedTanggalPemasukan = financial.intendedTanggalPemasukan,
                hutangWajib = financial.hutangWajib,
                estimasiTabungan = financial.estimasiTabungan,
                danaDarurat = financial.danaDarurat,
                budgetAllocation = com.mauwealthy.web.dto.BudgetAllocationPayload(
                    mode = financial.budgetAllocation.mode,
                    pengeluaran = financial.budgetAllocation.pengeluaran,
                    wants = financial.budgetAllocation.wants,
                    savings = financial.budgetAllocation.savings,
                ),
                currentPengeluaranLimit = financial.currentPengeluaranLimit,
                currentPengeluaranUsed = financial.currentPengeluaranUsed,
                currentSisaSaldoPool = financial.currentSisaSaldoPool,
                lastCycleCarryOverSaldo = financial.lastCycleCarryOverSaldo,
                monthlyTopUp = com.mauwealthy.web.dto.MonthlyTopUpPayload(
                    cycleKey = financial.monthlyTopUp.cycleKey,
                    fromTabunganCount = financial.monthlyTopUp.fromTabunganCount,
                    totalFromTabungan = financial.monthlyTopUp.totalFromTabungan,
                    totalFromDanaDarurat = financial.monthlyTopUp.totalFromDanaDarurat,
                ),
                currentCycleStart = financial.currentCycleStart?.toString(),
                currentCycleEnd = financial.currentCycleEnd?.toString(),
            )
        },
        streak = entity.streak?.let { streak ->
            com.mauwealthy.web.dto.StreakPayload(
                current = streak.current,
                longest = streak.longest,
                lastActiveDate = streak.lastActiveDate?.toString(),
                freezeUsed = streak.freezeUsed,
            )
        },
        debts = entity.debts.map(::toDebtPayload),
    )

    private fun toDebtPayload(debt: Debt): DebtPayload = DebtPayload(
        id = debt.id,
        name = debt.name,
        category = debt.category,
        debtType = debt.debtType,
        principalAmount = debt.principalAmount,
        remainingAmount = debt.remainingAmount,
        monthlyInstallment = debt.monthlyInstallment,
        dueDay = debt.dueDay,
        notes = debt.notes,
    )

    private fun parseDateOrThrow(value: String): LocalDate = try {
        LocalDate.parse(value)
    } catch (_: Exception) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must use ISO format yyyy-MM-dd")
    }

    private fun parseInstantOrNull(value: String?): Instant? =
        value?.let {
            try {
                Instant.parse(it)
            } catch (_: Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Timestamp must use ISO format")
            }
        }

    private fun parseLocalDateOrNull(value: String?): LocalDate? =
        value?.let {
            try {
                LocalDate.parse(it)
            } catch (_: Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must use ISO format yyyy-MM-dd")
            }
        }
}

