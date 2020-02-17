package ru.scp.quiz.service.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.model.dto.UserDto
import ru.scp.quiz.repository.auth.UserNotFoundException
import ru.scp.quiz.repository.auth.UsersRepository


@Service
class UserServiceImpl : UserService {

    @Autowired
    private lateinit var repository: UsersRepository

    override fun findAll() = repository.findAll().toList()

    override fun getByUsername(username: String) = repository.findOneByMyUsername(username)

    override fun getById(id: Long) = repository.getOne(id) ?: throw UserNotFoundException()

    override fun getByIdDto(id: Long): UserDto = repository.getOneAsUserDto(id) ?: throw UserNotFoundException()

    override fun insert(user: User): User = repository.save(user)

    override fun insert(users: List<User>): List<User> = repository.saveAll(users)

    override fun update(user: User): User = repository.save(user)

    override fun updateAvatarUrl(userId: Long, avatarUrl: String): UserDto {
        repository.updateAvatarUrl(userId, avatarUrl)
        return repository.getOneAsUserDto(userId)!!
    }

    override fun loadUserByUsername(username: String) = repository.findOneByMyUsername(username)

    override fun getByProviderId(id: String, provider: ScpQuizConstants.SocialProvider) = when (provider) {
        ScpQuizConstants.SocialProvider.GOOGLE -> repository.findOneByGoogleId(id)
        ScpQuizConstants.SocialProvider.FACEBOOK -> repository.findOneByFacebookId(id)
        ScpQuizConstants.SocialProvider.VK -> repository.findOneByVkId(id)
    }

    override fun deleteById(id: Long): Boolean {
        repository.deleteById(id)
        return true
    }
}