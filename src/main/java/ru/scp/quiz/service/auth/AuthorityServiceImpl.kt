package ru.scp.quiz.service.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.scp.quiz.bean.auth.Authority
import ru.scp.quiz.repository.auth.AuthoritiesRepository


@Service
class AuthorityServiceImpl : AuthorityService {

    @Autowired
    private lateinit var repository: AuthoritiesRepository

    override fun findAll(): List<Authority> = repository.findAll().toList()

    override fun insert(authority: Authority): Authority = repository.save(authority)

    override fun insert(authorities: List<Authority>): List<Authority> = repository.saveAll(authorities)

    override fun deleteByUserId(userId: Long) = repository.deleteByUserId(userId)
}