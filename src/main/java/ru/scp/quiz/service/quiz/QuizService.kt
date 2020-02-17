package ru.scp.quiz.service.quiz

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.bean.quiz.Quiz
import ru.scp.quiz.model.dto.QuizDto

interface QuizService {

    fun findAll(): List<Quiz>

    fun findAllIds(): List<Long>

    fun findAllWithUsers(): List<QuizDto>

    fun findAllSorted(fieldToSortBy: String, ascending: Boolean): List<Quiz>

    fun findOneById(quizId: Long): Quiz

    fun getFullCompleteLevelsQuizIds(userId: Long): List<Long>

    fun findOneByScpNumber(scpNumber: String): Quiz?

    fun findOneByImageUrl(imageUrl: String): Quiz?

    fun save(question: Quiz): QuizDto

    fun findAllByQuizTranslationsLangCode(langCode: String): Set<Quiz>

    fun findOneByQuizTranslationsId(quizTranslationId: Long): Quiz

    fun deleteById(id: Long): Boolean

    fun deleteAuthorIdForUserId(userId: Long)

    fun getQuizzesForPaging(offset: Int, limit: Int, quizIds: List<Long>): List<QuizDto>
}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Quiz not found")
class QuizNotFoundException : RuntimeException()

@ResponseStatus(value = HttpStatus.CONFLICT)
class QuizAlreadyExistsException(override val message: String?) : RuntimeException(message)