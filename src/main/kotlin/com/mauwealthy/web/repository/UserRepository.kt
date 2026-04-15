package com.mauwealthy.web.repository

import com.mauwealthy.web.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>