package com.mauwealthy.web.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "users")
class User(
    @Id
    @Column(name = "id", columnDefinition = "varchar(255)")
    var id: String = "",

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var phone: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false)
    var onboardingCompleted: Boolean = false,

    @Column(nullable = false)
    var level: Int = 1,
) {
    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var investmentWatchlist: InvestmentWatchlist? = null

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var journal: Journal? = null

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var financialData: FinancialData? = null

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var streak: Streak? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var debts: MutableList<Debt> = mutableListOf()
}

@Entity
@Table(name = "investment_watchlists")
class InvestmentWatchlist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var selectedSymbol: String? = null,

    var updatedAt: Instant? = null,
) {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User? = null

    @OneToMany(mappedBy = "watchlist", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<WatchlistItem> = mutableListOf()
}

@Entity
@Table(name = "watchlist_items")
class WatchlistItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var symbol: String = "",

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var type: String = "",

    @Column(nullable = false)
    var region: String = "",

    @Column(nullable = false)
    var currency: String = "",

    var createdAt: Instant? = null,
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_id", nullable = false)
    var watchlist: InvestmentWatchlist? = null
}

@Entity
@Table(name = "journals")
class Journal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var nextChatMessageId: Int = 1,
) {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User? = null

    @OneToMany(mappedBy = "journal", cascade = [CascadeType.ALL], orphanRemoval = true)
    var chatMessages: MutableList<ChatMessage> = mutableListOf()

    @OneToMany(mappedBy = "journal", cascade = [CascadeType.ALL], orphanRemoval = true)
    var expenses: MutableList<ExpenseEntry> = mutableListOf()

    @OneToMany(mappedBy = "journal", cascade = [CascadeType.ALL], orphanRemoval = true)
    var incomes: MutableList<IncomeEntry> = mutableListOf()
}

@Entity
@Table(name = "chat_messages")
class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var messageId: Int = 0,

    @Column(nullable = false)
    var sender: String = "",

    @Column(nullable = false, length = 1000)
    var text: String = "",

    @Column(nullable = false)
    var time: String = "",

    @Column(nullable = false)
    var chatDate: LocalDate = LocalDate.now(),
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    var journal: Journal? = null
}

@Entity
@Table(name = "journal_expenses")
class ExpenseEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var amount: Long = 0,

    @Column(nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var category: String = "",

    @Column(nullable = false)
    var expenseDate: LocalDate = LocalDate.now(),
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    var journal: Journal? = null
}

@Entity
@Table(name = "journal_incomes")
class IncomeEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var amount: Long = 0,

    @Column(nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var source: String = "",

    @Column(nullable = false)
    var incomeDate: LocalDate = LocalDate.now(),
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    var journal: Journal? = null
}

@Entity
@Table(name = "financial_data")
class FinancialData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var pendapatan: Long = 0,

    @Column(nullable = false)
    var pengeluaranWajib: Long = 0,

    @Column(nullable = false)
    var tanggalPemasukan: Int = 1,

    @Column(nullable = false)
    var intendedTanggalPemasukan: Int = 1,

    @Column(nullable = false)
    var hutangWajib: Long = 0,

    @Column(nullable = false)
    var estimasiTabungan: Long = 0,

    @Column(nullable = false)
    var danaDarurat: Long = 0,

    @Column(nullable = false)
    var currentPengeluaranLimit: Long = 0,

    @Column(nullable = false)
    var currentPengeluaranUsed: Long = 0,

    @Column(nullable = false)
    var currentSisaSaldoPool: Long = 0,

    @Column(nullable = false)
    var lastCycleCarryOverSaldo: Long = 0,

    var currentCycleStart: LocalDate? = null,

    var currentCycleEnd: LocalDate? = null,
) {
    @Embedded
    var budgetAllocation: BudgetAllocation = BudgetAllocation()

    @Embedded
    var monthlyTopUp: MonthlyTopUp = MonthlyTopUp()

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User? = null
}

@Embeddable
class BudgetAllocation(
    var mode: Int = 0,
    var pengeluaran: Int = 0,
    var wants: Int = 0,
    var savings: Int = 0,
)

@Embeddable
class MonthlyTopUp(
    var cycleKey: String? = null,
    var fromTabunganCount: Int = 0,
    var totalFromTabungan: Long = 0,
    var totalFromDanaDarurat: Long = 0,
)

@Entity
@Table(name = "streaks")
class Streak(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var current: Int = 0,

    @Column(nullable = false)
    var longest: Int = 0,

    var lastActiveDate: LocalDate? = null,

    @Column(nullable = false)
    var freezeUsed: Boolean = false,
) {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User? = null
}

@Entity
@Table(name = "debts")
class Debt(
    @Id
    @Column(name = "id", columnDefinition = "varchar(255)")
    var id: String = "",

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var category: String = "",

    @Column(nullable = false)
    var debtType: String = "",

    @Column(nullable = false)
    var principalAmount: Long = 0,

    @Column(nullable = false)
    var remainingAmount: Long = 0,

    @Column(nullable = false)
    var monthlyInstallment: Long = 0,

    @Column(nullable = false)
    var dueDay: Int = 1,

    @Column(nullable = false, length = 1000)
    var notes: String = "",
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null
}
