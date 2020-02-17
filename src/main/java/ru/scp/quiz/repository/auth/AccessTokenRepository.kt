package ru.scp.quiz.repository.auth

import org.springframework.data.jpa.repository.JpaRepository
import ru.scp.quiz.bean.auth.OAuthAccessToken

interface AccessTokenRepository : JpaRepository<OAuthAccessToken, String>