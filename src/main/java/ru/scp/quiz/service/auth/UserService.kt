package ru.scp.quiz.service.auth

import org.springframework.security.core.userdetails.UserDetailsService
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.model.dto.UserDto
import javax.transaction.Transactional

interface UserService : UserDetailsService {
    fun findAll(): List<User>
    fun getById(id: Long): User
    fun getByIdDto(id: Long): UserDto
    fun getByUsername(username: String): User?
    override fun loadUserByUsername(username: String): User?
    fun getByProviderId(id: String, provider: ScpQuizConstants.SocialProvider): User?

    @Transactional
    fun insert(user: User): User

    fun insert(users: List<User>): List<User>

    fun update(user: User): User

    fun updateAvatarUrl(userId: Long, avatarUrl: String): UserDto

    fun deleteById(id: Long): Boolean
}