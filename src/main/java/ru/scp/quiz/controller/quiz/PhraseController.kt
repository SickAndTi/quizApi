package ru.scp.quiz.controller.quiz

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.scp.quiz.ScpQuizConstants.Path.PHRASES
import ru.scp.quiz.ScpQuizConstants.Path.QUIZ
import ru.scp.quiz.ScpQuizConstants.Path.TRANSLATIONS
import ru.scp.quiz.bean.auth.AuthorityType
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.bean.quiz.QuizTranslationPhrase
import ru.scp.quiz.controller.AccessDeniedException
import ru.scp.quiz.model.dto.QuizTranslationDto
import ru.scp.quiz.model.dto.QuizTranslationPhraseDto
import ru.scp.quiz.service.quiz.QuizTranslationPhraseAlreadyExistsException
import ru.scp.quiz.service.quiz.QuizTranslationPhraseService
import ru.scp.quiz.service.quiz.QuizTranslationService


@RestController
@RequestMapping("/$QUIZ/$TRANSLATIONS/$PHRASES")
class PhraseController {

    @Autowired
    lateinit var quizTranslationService: QuizTranslationService

    @Autowired
    lateinit var quizTranslationPhrasesService: QuizTranslationPhraseService

    @Suppress("unused")
    @DeleteMapping("/delete/{id}")
    fun deleteQuizTranslationPhrase(
            @PathVariable(value = "id") id: Long,
            @AuthenticationPrincipal user: User
    ): Boolean {
        //check if user is ADMIN or author of deleting object
        val isAdmin = user.userAuthorities.any { it.authority == AuthorityType.ADMIN.name }
        val quizTranslationPhrase = quizTranslationPhrasesService.findOneById(id)
        if (!isAdmin) {
            if (quizTranslationPhrase.authorId != user.id) {
                throw AccessDeniedException()
            }
        }

        return quizTranslationPhrasesService.deleteById(id)
    }

    @GetMapping("/all")
    fun showAllTranslationPhrases() = quizTranslationPhrasesService.findAll()

    @GetMapping("/{id}")
    fun getQuizTranslationPhraseById(@PathVariable(value = "id") id: Long) = quizTranslationPhrasesService.findOneById(id)

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/approve")
    fun approveQuizTranslationPhrase(
            @PathVariable(value = "id") id: Long,
            @RequestParam(value = "approve") approve: Boolean,
            @AuthenticationPrincipal user: User
    ): QuizTranslationPhraseDto {
        val quizTranslationPhrase = quizTranslationPhrasesService.findOneById(id)
        quizTranslationPhrase.approved = approve
        quizTranslationPhrase.approverId = user.id
        return quizTranslationPhrasesService.save(quizTranslationPhrase)
    }

    @PostMapping("/add")
    fun addTranslationPhrase(
            @RequestParam(value = "quizTranslationId") quizTranslationId: Long,
            @RequestParam(value = "text") text: String,
            @AuthenticationPrincipal user: User
    ): QuizTranslationDto {
        //check params
        if (text.isEmpty()) {
            throw IllegalArgumentException("text is empty!")
        }
        //check if it's exiting already
        if (quizTranslationPhrasesService.findOneByTranslation(text) != null) {
            throw QuizTranslationPhraseAlreadyExistsException()
        }

        val quizTranslationPhrase = QuizTranslationPhrase(
                authorId = user.id!!,
                translation = text
        )

        val quizTranslation = quizTranslationService.findOneById(quizTranslationId)
        quizTranslation.quizTranslationPhrases += quizTranslationPhrase

        return quizTranslationService.save(quizTranslation)
    }
}