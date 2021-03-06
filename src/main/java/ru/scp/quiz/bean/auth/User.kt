package ru.scp.quiz.bean.auth

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.http.HttpStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.model.dto.LeaderboardDto
import ru.scp.quiz.model.dto.UserDto
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "users")

//see https://stackoverflow.com/questions/49225739/namednativequery-with-sqlresultsetmapping-for-non-entity
@SqlResultSetMapping(name = "UserDtoResult", classes = [
    ConstructorResult(targetClass = UserDto::class,
            columns = [
                ColumnResult(name = "id", type = Long::class),
                ColumnResult(name = "fullName"),
                ColumnResult(name = "avatar"),
                ColumnResult(name = "score", type = Long::class)
            ])
])
@NamedNativeQuery(name = "User.getOneAsUserDto",
        resultSetMapping = "UserDtoResult",
        query = "SELECT " +
                "id, " +
                "full_name as fullName, " +
                "avatar, " +
                "score " +
                "FROM users u " +
                "WHERE u.id = :userId")

data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @Column(name = "name_first")
        var nameFirst: String? = null,
        @Column(name = "name_second")
        var nameSecond: String? = null,
        @Column(name = "name_third")
        var nameThird: String? = null,
        @Column(name = "full_name")
        var fullName: String? = null,
        @Column(name = "username", unique = true)
        var myUsername: String,
        @Column(name = "password")
        var myPassword: String,
        var avatar: String?,
        @Column(name = "score")
        var score: Long = 0,
        val enabled: Boolean = true,
        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "userId", fetch = FetchType.EAGER)
        var userAuthorities: Set<Authority>,
        @field:CreationTimestamp
        val created: Timestamp? = null,
        @field:UpdateTimestamp
        val updated: Timestamp? = null,
        //social login fields
        @Column(name = "facebook_id")
        var facebookId: String? = null,
        @Column(name = "google_id")
        var googleId: String? = null,
        @Column(name = "vk_id")
        var vkId: String? = null
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
            userAuthorities.map { SimpleGrantedAuthority(it.authority) }.toMutableList()

    override fun isEnabled() = enabled

    override fun getUsername() = myUsername

    override fun isCredentialsNonExpired() = true

    override fun getPassword() = myPassword

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true
}

fun User.toDto() = UserDto(
        id = id!!,
        avatar = avatar,
        score = score,
        fullName = fullNametoDto(fullName, nameFirst, nameSecond, nameThird)
)

fun fullNametoDto(fullName: String?, nameFirst: String?, nameSecond: String?, nameThird: String?): String? {
    if (fullName == null) {
        if (nameFirst == null && nameSecond == null && nameThird != null) {
            return nameThird
        }
        if (nameFirst == null && nameSecond != null && nameThird != null) {
            return "$nameSecond $nameThird"
        }
        if (nameFirst == null && nameSecond != null && nameThird == null) {
            return nameSecond
        }
        if (nameFirst != null && nameSecond == null && nameThird == null) {
            return nameFirst
        }
        if (nameFirst != null && nameSecond == null && nameThird != null) {
            return "$nameFirst $nameThird"
        }
        if (nameFirst != null && nameSecond != null && nameThird == null) {
            return "$nameFirst $nameSecond"
        }
        if (nameFirst != null && nameSecond != null && nameThird != null) {
            return "$nameFirst $nameSecond $nameThird"
        } else {
            return null
        }
    } else {
        return fullName
    }
}

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User with this email already exists")
class UserAlreadyExistsException : RuntimeException()