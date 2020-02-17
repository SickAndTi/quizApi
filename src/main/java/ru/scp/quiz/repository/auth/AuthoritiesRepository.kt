package ru.scp.quiz.repository.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.scp.quiz.bean.auth.Authority

@Repository
interface AuthoritiesRepository : JpaRepository<Authority, Long> {

    fun deleteByUserId(userId: Long)
}