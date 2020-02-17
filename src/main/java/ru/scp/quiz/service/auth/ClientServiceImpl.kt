package ru.scp.quiz.service.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.stereotype.Service
import ru.scp.quiz.bean.auth.ClientNotFoundError
import ru.scp.quiz.bean.auth.OAuthClientDetails
import ru.scp.quiz.repository.auth.ClientDetailsRepository


@Service
class ClientServiceImpl : ClientService {

    @Autowired
    private lateinit var repository: ClientDetailsRepository

    override fun loadClientByClientId(clientId: String): ClientDetails {
        return repository.getOne(clientId) ?: throw ClientNotFoundError()
    }

    override fun findAll(): List<OAuthClientDetails> = repository.findAll()
}