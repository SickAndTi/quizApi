package ru.scp.quiz.service.quiz

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.scp.quiz.bean.quiz.QuizTranslation
import ru.scp.quiz.bean.quiz.toDto
import ru.scp.quiz.model.dto.QuizTranslationDto
import ru.scp.quiz.repository.auth.UsersRepository
import ru.scp.quiz.repository.quiz.QuizesTranslationsRepository


@Service
class QuizTranslationServiceImpl : QuizTranslationService {

    @Autowired
    private lateinit var repository: QuizesTranslationsRepository

    @Autowired
    private lateinit var usersRepository: UsersRepository

    override fun findAll(): List<QuizTranslation> {
        val data = repository.findAll()

        return data.toList()
    }

    override fun save(quizTranslation: QuizTranslation): QuizTranslationDto =
            repository.save(quizTranslation).toDto(usersRepository)

    override fun findOneByTextAndLangCode(text: String, langCode: String) =
            repository.findOneByTranslationAndLangCode(text, langCode)

    override fun findOneByQuizIdAndLangCode(quizId: Long, langCode: String) =
            repository.findByQuizAndLangCode(quizId, langCode)

    override fun findOneById(quizTranslationId: Long) =
            repository.findOneById(quizTranslationId) ?: throw QuizTranslationNotFoundException()

    override fun deleteById(id: Long): Boolean {
        repository.deleteById(id)
        return true
    }

    override fun updateDescription(quizTranslationId: Long, description: String): QuizTranslationDto {
        repository.updateDescription(quizTranslationId, description)
        return repository.getOne(quizTranslationId).toDto(usersRepository)
    }

    override fun deleteAuthorIdForUserId(userId: Long) =
            repository.deleteAuthorIdForUserId(userId)
}