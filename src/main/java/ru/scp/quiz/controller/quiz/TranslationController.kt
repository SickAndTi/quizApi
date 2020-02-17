package ru.scp.quiz.controller.quiz

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.scp.quiz.ScpQuizConstants.Path.QUIZ
import ru.scp.quiz.ScpQuizConstants.Path.TRANSLATIONS
import ru.scp.quiz.bean.auth.AuthorityType
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.bean.quiz.QuizTranslation
import ru.scp.quiz.controller.AccessDeniedException
import ru.scp.quiz.model.dto.QuizDto
import ru.scp.quiz.model.dto.QuizTranslationDto
import ru.scp.quiz.service.quiz.QuizService
import ru.scp.quiz.service.quiz.QuizTranslationAlreadyExistsException
import ru.scp.quiz.service.quiz.QuizTranslationService


@RestController
@RequestMapping("/$QUIZ/$TRANSLATIONS")
class TranslationController {

    @Autowired
    lateinit var quizService: QuizService

    @Autowired
    lateinit var quizTranslationService: QuizTranslationService

    @Suppress("unused")
    @DeleteMapping("/delete/{id}")
    fun deleteQuizTranslation(
            @PathVariable(value = "id") id: Long,
            @AuthenticationPrincipal user: User
    ): Boolean {
        //check if user is ADMIN or author of deleting object
        val isAdmin = user.userAuthorities.any { it.authority == AuthorityType.ADMIN.name }
        val quizTranslation = quizTranslationService.findOneById(id)
        if (!isAdmin) {
            if (quizTranslation.authorId != user.id) {
                throw AccessDeniedException()
            }
        }

        return quizTranslationService.deleteById(id)
    }

    @GetMapping("/all")
    fun showAllTranslations() = quizTranslationService.findAll()

    @GetMapping("/{id}")
    fun getQuizTranslationById(@PathVariable(value = "id") id: Long) = quizTranslationService.findOneById(id)

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/approve")
    fun approveQuizTranslation(
            @PathVariable(value = "id") id: Long,
            @RequestParam(value = "approve") approve: Boolean,
            @AuthenticationPrincipal user: User
    ): QuizTranslationDto {
        val quizTranslation = quizTranslationService.findOneById(id)
        quizTranslation.approved = approve
        quizTranslation.approverId = user.id
        return quizTranslationService.save(quizTranslation)
    }

    @PostMapping("/add")
    fun addTranslation(
            @RequestParam(value = "quizId") quizId: Long,
            @RequestParam(value = "langCode") langCode: String,
            @RequestParam(value = "text") text: String,
            @RequestParam(value = "description") description: String,
            @AuthenticationPrincipal user: User
    ): QuizDto {
        //check params
        if (description.isEmpty()) {
            throw IllegalArgumentException("description is empty!")
        }
        if (text.isEmpty()) {
            throw IllegalArgumentException("text is empty!")
        }
        if (langCode.isEmpty()) {
            throw IllegalArgumentException("langCode is empty!")
        }

        //check if Quiz exists
        val quiz = quizService.findOneById(quizId)
        //check if it's exiting already
        if (quizTranslationService.findOneByQuizIdAndLangCode(quizId, langCode) != null) {
            throw QuizTranslationAlreadyExistsException("Such langCode already exists for this quizId!")
        }

        val quizTranslation = QuizTranslation(
                authorId = user.id!!,
                langCode = langCode,
                translation = text,
                description = description
        )
        quiz.quizTranslations += quizTranslation
        return quizService.save(quiz)
    }

    @PostMapping("/{quizTranslationId}/update")
    fun updateTranslationDescription(
            @PathVariable(value = "quizTranslationId") quizTranslationId: Long,
            @RequestParam(value = "description") description: String
    ) = quizTranslationService.updateDescription(quizTranslationId, description)
}