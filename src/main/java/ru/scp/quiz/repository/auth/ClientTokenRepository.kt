package ru.scp.quiz.repository.auth

import org.springframework.data.jpa.repository.JpaRepository
import ru.scp.quiz.bean.auth.OAuthClientToken

interface ClientTokenRepository : JpaRepository<OAuthClientToken, String>