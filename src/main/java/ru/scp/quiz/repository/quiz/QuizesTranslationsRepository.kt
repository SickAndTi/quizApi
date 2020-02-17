package ru.scp.quiz.repository.quiz

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.scp.quiz.bean.quiz.QuizTranslation

@Repository
interface QuizesTranslationsRepository : JpaRepository<QuizTranslation, Long> {

    fun findOneById(id: Long): QuizTranslation?

    fun findOneByTranslationAndLangCode(text: String, langCode: String): QuizTranslation?

    @Query("SELECT * FROM quiz_translations WHERE quiz=:quizId AND lang_code=:langCode", nativeQuery = true)
    fun findByQuizAndLangCode(quizId: Long, langCode: String): QuizTranslation?

    @Modifying
    @Query("UPDATE QuizTranslation q SET q.description = ?2 WHERE q.id = ?1")
    @Transactional
    fun updateDescription(quizTranslationId: Long, description: String): Int

    @Transactional
    @Modifying
    @Query("UPDATE QuizTranslation q SET q.authorId=NULL WHERE q.authorId = ?1")
    fun deleteAuthorIdForUserId(userId: Long)
}