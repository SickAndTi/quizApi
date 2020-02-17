package ru.scp.quiz.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.auth.AuthorityType
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.model.dto.UserDto
import ru.scp.quiz.service.auth.AccessTokenServices
import ru.scp.quiz.service.auth.AuthorityService
import ru.scp.quiz.service.auth.UserService
import ru.scp.quiz.service.quiz.QuizService
import ru.scp.quiz.service.quiz.QuizTranslationPhraseService
import ru.scp.quiz.service.quiz.QuizTranslationService


@RestController
@RequestMapping("/${ScpQuizConstants.Path.USER}")
class UserController {

    @Autowired
    lateinit var accessTokenServices: AccessTokenServices

    @Autowired
    lateinit var authorityService: AuthorityService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var quizService: QuizService

    @Autowired
    lateinit var quizTranslationService: QuizTranslationService

    @Autowired
    lateinit var quizTranslationPhraseService: QuizTranslationPhraseService

    @GetMapping("/me")
    fun showMe(
            @AuthenticationPrincipal user: User,
            @RequestParam(value = "showFull") showFull: Boolean = false
    ) = if (showFull) userService.getById(user.id!!) else user

    @GetMapping("/meClient")
    fun showMeClient(
            @AuthenticationPrincipal user: User
    ): UserDto = userService.getByIdDto(user.id!!)

    @GetMapping("/score")
    fun getUserScore(
            @AuthenticationPrincipal user: User
    ) = userService.getById(user.id!!).score

    @GetMapping("/all")
    fun showAllUsers() = userService.findAll()

    @PostMapping("/{userId}/updateAvatarUrl")
    fun updateUserAvatarUrl(
            @PathVariable(value = "userId") userId: Long,
            @RequestParam(value = "avatarUrl") avatarUrl: String,
            @AuthenticationPrincipal user: User
    ): UserDto {
        //check if user is ADMIN or target of edit operation
        val isAdmin = user.userAuthorities.any { it.authority == AuthorityType.ADMIN.name }
        if (!isAdmin || userId != user.id) {
            throw AccessDeniedException()
        }

        return userService. updateAvatarUrl(userId, avatarUrl)
    }

    @Suppress("unused")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    fun deleteUser(@PathVariable(value = "id") id: Long): Boolean {
        accessTokenServices.deleteAllTokensByUserId(id)

        authorityService.deleteByUserId(id)

        quizService.deleteAuthorIdForUserId(id)
        quizTranslationService.deleteAuthorIdForUserId(id)
        quizTranslationPhraseService.deleteAuthorIdForUserId(id)

        return userService.deleteById(id)
    }
}

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "You do not have rights for this action")
class AccessDeniedException : RuntimeException()