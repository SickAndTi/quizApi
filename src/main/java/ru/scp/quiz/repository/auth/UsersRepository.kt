package ru.scp.quiz.repository.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.model.dto.UserDto

interface UsersRepository : JpaRepository<User, Long> {
    fun findOneByMyUsername(username: String): User?
    fun findOneById(id: Long): User?
    fun findOneByGoogleId(id: String): User?
    fun findOneByFacebookId(id: String): User?
    fun findOneByVkId(id: String): User?

    @Modifying
    @Query("UPDATE User u SET u.avatar = ?2 WHERE u.id = ?1")
    @Transactional
    fun updateAvatarUrl(userId: Long, avatarUrl: String): Int

    //see https://stackoverflow.com/a/50968131/3212712
    @Query(nativeQuery = true)
    fun getOneAsUserDto(userId: Long): UserDto?

    @Query("SELECT * FROM users ORDER BY score DESC", nativeQuery = true)
    fun getFullLeaderboard(): List<User>

    @Query("SELECT * FROM users ORDER BY score DESC OFFSET :offset LIMIT :limit", nativeQuery = true)
    fun getPartLeaderboard(offset: Int, limit: Int): List<User>

    @Query("SELECT position FROM (select *, ROW_NUMBER() OVER(ORDER BY score DESC) AS position FROM users) result WHERE id = :userId", nativeQuery = true)
    fun getUserPositionInLeaderboard(userId: Long): Int
}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such user")
class UserNotFoundException : RuntimeException()