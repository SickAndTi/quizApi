package ru.scp.quiz.service.auth

import org.springframework.security.oauth2.provider.ClientDetailsService
import ru.scp.quiz.bean.auth.OAuthClientDetails

interface ClientService : ClientDetailsService {
    fun findAll(): List<OAuthClientDetails>
}