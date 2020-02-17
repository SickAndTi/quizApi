package ru.scp.quiz.service.quiz

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.bean.quiz.QuizTranslationPhrase
import ru.scp.quiz.model.dto.QuizTranslationPhraseDto

interface QuizTranslationPhraseService {
    fun findAll(): List<QuizTranslationPhrase>

    fun save(quizTranslationPhrase: QuizTranslationPhrase): QuizTranslationPhraseDto

    fun findOneByTranslation(text: String): QuizTranslationPhrase?

    fun findOneByQuizTranslationId(quizTranslationId: Long): QuizTranslationPhrase

    fun findOneById(quizTranslationId: Long): QuizTranslationPhrase

    fun deleteById(id: Long) : Boolean

    fun deleteAuthorIdForUserId(userId: Long)
}

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "QuizTranslationPhrase already exists")
class QuizTranslationPhraseAlreadyExistsException : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "QuizTranslationPhrase not found")
class QuizTranslationPhraseNotFoundException : RuntimeException()