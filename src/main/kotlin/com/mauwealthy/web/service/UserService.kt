package com.mauwealthy.web.service

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
import com.mauwealthy.web.entity.BudgetAllocation
import com.mauwealthy.web.entity.ChatMessage
import com.mauwealthy.web.entity.Debt
import com.mauwealthy.web.entity.ExpenseEntry
import com.mauwealthy.web.entity.FinancialData
import com.mauwealthy.web.entity.IncomeEntry
import com.mauwealthy.web.entity.InvestmentWatchlist
import com.mauwealthy.web.entity.Journal
import com.mauwealthy.web.entity.MonthlyTopUp
import com.mauwealthy.web.entity.SavingsAllocation
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
    fun patchFinancialData(id: String, payload: FinancialDataPatchPayload): UserPayload {
        val user = getUserOrThrow(id)
        val financialData = user.financialData ?: FinancialData().also {
            it.user = user
            user.financialData = it
        }

        payload.pendapatan?.let { financialData.pendapatan = it }
        payload.pengeluaranWajib?.let { financialData.pengeluaranWajib = it }
        payload.tanggalPemasukan?.let { financialData.tanggalPemasukan = it }
        payload.intendedTanggalPemasukan?.let { financialData.intendedTanggalPemasukan = it }
        payload.hutangWajib?.let { financialData.hutangWajib = it }
        payload.estimasiTabungan?.let { financialData.estimasiTabungan = it }
        payload.danaDarurat?.let { financialData.danaDarurat = it }
        payload.danaInvestasi?.let { financialData.danaInvestasi = it }
        payload.currentPengeluaranLimit?.let { financialData.currentPengeluaranLimit = it }
        payload.currentPengeluaranUsed?.let { financialData.currentPengeluaranUsed = it }
        payload.currentSisaSaldoPool?.let { financialData.currentSisaSaldoPool = it }
        payload.lastCycleCarryOverSaldo?.let { financialData.lastCycleCarryOverSaldo = it }

        payload.currentCycleStart?.let { financialData.currentCycleStart = parseLocalDateOrNull(it) }
        payload.currentCycleEnd?.let { financialData.currentCycleEnd = parseLocalDateOrNull(it) }

        payload.budgetAllocation?.let { budgetPatch ->
            val budgetAllocation = financialData.budgetAllocation
            budgetPatch.mode?.let { budgetAllocation.mode = it }
            budgetPatch.pengeluaran?.let { budgetAllocation.pengeluaran = it }
            budgetPatch.wants?.let { budgetAllocation.wants = it }
            budgetPatch.savings?.let { budgetAllocation.savings = it }
        }

        payload.monthlyTopUp?.let { topUpPatch ->
            val monthlyTopUp = financialData.monthlyTopUp
            topUpPatch.cycleKey?.let { monthlyTopUp.cycleKey = it }
            topUpPatch.fromTabunganCount?.let { monthlyTopUp.fromTabunganCount = it }
            topUpPatch.totalFromTabungan?.let { monthlyTopUp.totalFromTabungan = it }
            topUpPatch.totalFromDanaDarurat?.let { monthlyTopUp.totalFromDanaDarurat = it }
        }

        payload.savingsAllocation?.let { allocationPatch ->
            val savingsAllocation = financialData.savingsAllocation
            allocationPatch.tabungan?.let { savingsAllocation.tabungan = it }
            allocationPatch.danaDarurat?.let { savingsAllocation.danaDarurat = it }
            allocationPatch.danaInvestasi?.let { savingsAllocation.danaInvestasi = it }
        }

        payload.savingsAllocationDelta?.let { deltaPatch ->
            val savingsAllocation = financialData.savingsAllocation
            deltaPatch.tabungan?.let { savingsAllocation.tabungan += it }
            deltaPatch.danaDarurat?.let { savingsAllocation.danaDarurat += it }
            deltaPatch.danaInvestasi?.let { savingsAllocation.danaInvestasi += it }
        }

        payload.investmentTracking?.cycleAmounts?.let { cycleAmountsPatch ->
            financialData.investmentCycleAmounts.putAll(cycleAmountsPatch)
        }

        val saved = userRepository.save(user)
        return toPayload(saved)
    }

    @Transactional
    fun patchWatchlist(id: String, payload: InvestmentWatchlistPatchPayload): UserPayload {
        val user = getUserOrThrow(id)
        val watchlist = user.investmentWatchlist ?: InvestmentWatchlist().also {
            it.user = user
            user.investmentWatchlist = it
        }

        payload.selectedSymbol?.let { watchlist.selectedSymbol = it }
        payload.updatedAt?.let { watchlist.updatedAt = parseInstantOrNull(it) }
        payload.items?.let { itemPayloads ->
            watchlist.items.clear()
            itemPayloads.forEach { itemPayload ->
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

        val saved = userRepository.save(user)
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
                    parsedText = it.parsedText,
                    parsedNominal = it.parsedNominal,
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

        // Auto-parse nominal + text only for user messages
        val parsed = if (request.sender == "user") ChatTextParser.parse(request.text) else ChatTextParser.ParseResult(null, null)

        val chat = ChatMessage(
            messageId = nextId,
            sender = request.sender,
            text = request.text,
            time = request.time,
            chatDate = parsedDate,
            parsedText = parsed.parsedText,
            parsedNominal = parsed.parsedNominal,
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
            parsedText = chat.parsedText,
            parsedNominal = chat.parsedNominal,
        )
    }

    fun findExpensesByDate(userId: String, date: String): List<ExpensePayload> {
        val parsedDate = parseDateOrThrow(date)
        val journal = getUserOrThrow(userId).journal
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Journal not found")

        return journal.expenses
            .filter { it.expenseDate == parsedDate }
            .map { ExpensePayload(amount = it.amount, description = it.description, category = it.category) }
    }

    @Transactional
    fun addExpense(userId: String, date: String, request: CreateExpenseRequest): ExpensePayload {
        val parsedDate = parseDateOrThrow(date)
        val user = getUserOrThrow(userId)
        val journal = user.journal ?: Journal().also {
            it.user = user
            user.journal = it
        }

        val expense = ExpenseEntry(
            amount = request.amount,
            description = request.description,
            category = request.category,
            expenseDate = parsedDate,
        )
        expense.journal = journal
        journal.expenses.add(expense)
        userRepository.save(user)

        return ExpensePayload(amount = expense.amount, description = expense.description, category = expense.category)
    }

    fun findIncomesByDate(userId: String, date: String): List<IncomePayload> {
        val parsedDate = parseDateOrThrow(date)
        val journal = getUserOrThrow(userId).journal
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Journal not found")

        return journal.incomes
            .filter { it.incomeDate == parsedDate }
            .map { IncomePayload(amount = it.amount, description = it.description, source = it.source) }
    }

    @Transactional
    fun addIncome(userId: String, date: String, request: CreateIncomeRequest): IncomePayload {
        val parsedDate = parseDateOrThrow(date)
        val user = getUserOrThrow(userId)
        val journal = user.journal ?: Journal().also {
            it.user = user
            user.journal = it
        }

        val income = IncomeEntry(
            amount = request.amount,
            description = request.description,
            source = request.source,
            incomeDate = parsedDate,
        )
        income.journal = journal
        journal.incomes.add(income)
        userRepository.save(user)

        return IncomePayload(amount = income.amount, description = income.description, source = income.source)
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
            // Reuse existing entity to avoid duplicate-insert (preserve DB id)
            val watchlist = existing?.investmentWatchlist ?: InvestmentWatchlist()
            watchlist.selectedSymbol = watchlistPayload.selectedSymbol
            watchlist.updatedAt = parseInstantOrNull(watchlistPayload.updatedAt)
            watchlist.user = user
            watchlist.items.clear()
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
            watchlist
        }

        user.journal = payload.journal?.let { journalPayload ->
            // Reuse existing entity to avoid duplicate-insert
            val journal = existing?.journal ?: Journal()
            journal.nextChatMessageId = journalPayload.nextChatMessageId
            journal.user = user
            journal.chatMessages.clear()
            journalPayload.chatByDate.forEach { (date, messages) ->
                val parsedDate = parseDateOrThrow(date)
                messages.forEach { message ->
                    val parsed = if (message.sender == "user") ChatTextParser.parse(message.text) else ChatTextParser.ParseResult(null, null)
                    val chat = ChatMessage(
                        messageId = message.id,
                        sender = message.sender,
                        text = message.text,
                        time = message.time,
                        chatDate = parsedDate,
                        parsedText = parsed.parsedText,
                        parsedNominal = parsed.parsedNominal,
                    )
                    chat.journal = journal
                    journal.chatMessages.add(chat)
                }
            }
            journal.expenses.clear()
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
            journal.incomes.clear()
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
            journal
        }

        user.financialData = payload.financialData?.let { financialPayload ->
            // Reuse existing entity to avoid duplicate-insert
            val financialData = existing?.financialData ?: FinancialData()
            financialData.pendapatan = financialPayload.pendapatan
            financialData.pengeluaranWajib = financialPayload.pengeluaranWajib
            financialData.tanggalPemasukan = financialPayload.tanggalPemasukan
            financialData.intendedTanggalPemasukan = financialPayload.intendedTanggalPemasukan
            financialData.hutangWajib = financialPayload.hutangWajib
            financialData.estimasiTabungan = financialPayload.estimasiTabungan
            financialData.danaDarurat = financialPayload.danaDarurat
            financialData.danaInvestasi = financialPayload.danaInvestasi
            financialData.currentPengeluaranLimit = financialPayload.currentPengeluaranLimit
            financialData.currentPengeluaranUsed = financialPayload.currentPengeluaranUsed
            financialData.currentSisaSaldoPool = financialPayload.currentSisaSaldoPool
            financialData.lastCycleCarryOverSaldo = financialPayload.lastCycleCarryOverSaldo
            financialData.currentCycleStart = parseLocalDateOrNull(financialPayload.currentCycleStart)
            financialData.currentCycleEnd = parseLocalDateOrNull(financialPayload.currentCycleEnd)
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
            financialData.savingsAllocation = SavingsAllocation(
                tabungan = financialPayload.savingsAllocation.tabungan,
                danaDarurat = financialPayload.savingsAllocation.danaDarurat,
                danaInvestasi = financialPayload.savingsAllocation.danaInvestasi,
            )
            financialData.investmentCycleAmounts.clear()
            financialData.investmentCycleAmounts.putAll(financialPayload.investmentTracking.cycleAmounts)
            financialData.user = user
            financialData
        }

        user.streak = payload.streak?.let { streakPayload ->
            // Reuse existing entity to avoid duplicate-insert
            val streak = existing?.streak ?: Streak()
            streak.current = streakPayload.current
            streak.longest = streakPayload.longest
            streak.lastActiveDate = parseLocalDateOrNull(streakPayload.lastActiveDate)
            streak.freezeUsed = streakPayload.freezeUsed
            streak.user = user
            streak
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
                                parsedText = it.parsedText,
                                parsedNominal = it.parsedNominal,
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
                danaInvestasi = financial.danaInvestasi,
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
                savingsAllocation = com.mauwealthy.web.dto.SavingsAllocationPayload(
                    tabungan = financial.savingsAllocation.tabungan,
                    danaDarurat = financial.savingsAllocation.danaDarurat,
                    danaInvestasi = financial.savingsAllocation.danaInvestasi,
                ),
                investmentTracking = com.mauwealthy.web.dto.InvestmentTrackingPayload(
                    cycleAmounts = financial.investmentCycleAmounts.toMap(),
                ),
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

