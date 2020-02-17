package ru.scp.quiz.repository.auth

import org.springframework.data.jpa.repository.JpaRepository
import ru.scp.quiz.bean.auth.OAuthClientDetails

interface ClientDetailsRepository : JpaRepository<OAuthClientDetails, String>