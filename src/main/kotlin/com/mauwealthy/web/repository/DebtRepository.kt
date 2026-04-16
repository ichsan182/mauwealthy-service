package com.mauwealthy.web.repository

import com.mauwealthy.web.entity.Debt
import org.springframework.data.jpa.repository.JpaRepository

interface DebtRepository : JpaRepository<Debt, String> {
    fun findAllByUserId(userId: String): List<Debt>
}

