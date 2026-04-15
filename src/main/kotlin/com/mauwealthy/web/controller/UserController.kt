package com.mauwealthy.web.controller

import com.mauwealthy.web.entity.User
import com.mauwealthy.web.repository.UserRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val repo: UserRepository
) {

    @PostMapping
    fun create(@RequestBody user: User) = repo.save(user)

    @GetMapping
    fun findAll() = repo.findAll()
}