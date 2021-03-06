package ru.scp.quiz.service.auth

import org.springframework.transaction.annotation.Transactional
import ru.scp.quiz.bean.auth.Authority

interface AuthorityService {
    fun findAll(): List<Authority>
    fun insert(authority: Authority): Authority?
    fun insert(authorities: List<Authority>): List<Authority>

    @Transactional
    fun deleteByUserId(userId: Long)
}