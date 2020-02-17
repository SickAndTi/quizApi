package ru.scp.quiz.repository.quiz

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.scp.quiz.bean.quiz.Quiz

@Repository
interface QuizzesRepository : JpaRepository<Quiz, Long> {

    fun findOneById(id: Long): Quiz?

    fun findAllByQuizTranslationsLangCode(langCode: String): Set<Quiz>

    fun findOneByQuizTranslationsId(quizTranslationId: Long): Quiz?

    fun findOneByScpNumber(scpNumber: String): Quiz?

    fun findOneByImageUrl(imageUrl: String): Quiz?

    @Transactional
    @Modifying
    @Query("UPDATE Quiz q SET q.authorId=NULL WHERE q.authorId = ?1")
    fun deleteAuthorIdForUserId(userId: Long)

    @Query("SELECT * FROM quiz WHERE id IN :quizIds ORDER BY id ASC OFFSET :offset LIMIT :limit", nativeQuery = true)
    fun getQuizzesForPaging(offset: Int, limit: Int, quizIds: List<Long>): List<Quiz>

    @Query("SELECT id FROM quiz", nativeQuery = true)
    fun findAllIds(): List<Long>
}
