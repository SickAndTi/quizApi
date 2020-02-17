package ru.scp.quiz.service.quiz

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.bean.quiz.QuizTranslation
import ru.scp.quiz.model.dto.QuizTranslationDto

interface QuizTranslationService {
    fun findAll(): List<QuizTranslation>

    fun save(quizTranslation: QuizTranslation): QuizTranslationDto

    fun findOneByQuizIdAndLangCode(quizId: Long, langCode: String): QuizTranslation?

    fun findOneByTextAndLangCode(text: String, langCode: String): QuizTranslation?

    fun findOneById(quizTranslationId: Long): QuizTranslation

    fun deleteById(id: Long) : Boolean

    fun updateDescription(quizTranslationId: Long, description: String): QuizTranslationDto

    fun deleteAuthorIdForUserId(userId: Long)
}

@ResponseStatus(value = HttpStatus.CONFLICT)
class QuizTranslationAlreadyExistsException(override val message: String?) : RuntimeException(message)

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "QuizTranslation not found")
class QuizTranslationNotFoundException : RuntimeException()