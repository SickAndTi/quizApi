package ru.scp.quiz.repository.quiz

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.scp.quiz.bean.quiz.QuizTranslationPhrase

@Repository
interface QuizesTranslationsPhrasesRepository : CrudRepository<QuizTranslationPhrase, Long> {

    fun findOneByTranslation(text: String): QuizTranslationPhrase?

    @Query("SELECT * FROM quiz_translation_phrases WHERE quiz_translation=:quizTranslationId", nativeQuery = true)
    fun findOneByQuizTranslationId(quizTranslationId: Long): QuizTranslationPhrase?

    fun findOneById(id: Long): QuizTranslationPhrase?

    @Transactional
    @Modifying
    @Query("UPDATE QuizTranslationPhrase q SET q.authorId=NULL WHERE q.authorId = ?1")
    fun deleteAuthorIdForUserId(userId: Long)
}