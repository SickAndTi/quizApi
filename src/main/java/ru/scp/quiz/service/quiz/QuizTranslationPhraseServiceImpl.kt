package ru.scp.quiz.service.quiz

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.scp.quiz.bean.quiz.QuizTranslationPhrase
import ru.scp.quiz.bean.quiz.toDto
import ru.scp.quiz.repository.auth.UsersRepository
import ru.scp.quiz.repository.quiz.QuizesTranslationsPhrasesRepository


@Service
class QuizTranslationPhraseServiceImpl : QuizTranslationPhraseService {

    @Autowired
    private lateinit var repository: QuizesTranslationsPhrasesRepository

    @Autowired
    private lateinit var usersRepository: UsersRepository

    override fun findAll(): List<QuizTranslationPhrase> = repository.findAll().toList()

    override fun save(quizTranslationPhrase: QuizTranslationPhrase) =
            repository.save(quizTranslationPhrase).toDto(usersRepository)

    override fun findOneByTranslation(text: String) =
            repository.findOneByTranslation(text)

    override fun findOneByQuizTranslationId(quizTranslationId: Long) =
            repository.findOneByQuizTranslationId(quizTranslationId) ?: throw QuizTranslationPhraseNotFoundException()

    override fun findOneById(quizTranslationId: Long) =
            repository.findOneById(quizTranslationId) ?: throw QuizTranslationPhraseNotFoundException()

    override fun deleteById(id: Long): Boolean {
        repository.deleteById(id)
        return true
    }

    override fun deleteAuthorIdForUserId(userId: Long) =
            repository.deleteAuthorIdForUserId(userId)
}