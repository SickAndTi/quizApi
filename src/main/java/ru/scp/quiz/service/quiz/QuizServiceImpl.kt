package ru.scp.quiz.service.quiz

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.quiz.Quiz
import ru.scp.quiz.bean.quiz.toDto
import ru.scp.quiz.model.dto.QuizDto
import ru.scp.quiz.repository.auth.UsersRepository
import ru.scp.quiz.repository.quiz.QuizzesRepository
import ru.scp.quiz.repository.transaction.QuizTransactionRepository
import ru.scp.quiz.service.transaction.QuizTransactionService
import java.util.*

@Service
class QuizServiceImpl : QuizService {

    @Autowired
    private lateinit var repository: QuizzesRepository

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Autowired
    lateinit var quizTransactionRepository: QuizTransactionRepository

    @Autowired
    private lateinit var quizTransactionService: QuizTransactionService


    override fun findAll(): List<Quiz> = repository.findAll()

    @Transactional
    override fun findAllWithUsers(): List<QuizDto> = repository.findAll().map { it.toDto(usersRepository) }

    @Transactional
    override fun getQuizzesForPaging(offset: Int, limit: Int, quizIds: List<Long>): List<QuizDto> =
            repository.getQuizzesForPaging(offset, limit, quizIds).map { it.toDto(usersRepository) }

    override fun findAllSorted(fieldToSortBy: String, ascending: Boolean): List<Quiz> =
            repository.findAll(Sort(if (ascending) Sort.Direction.ASC else Sort.Direction.DESC, fieldToSortBy))

    override fun findOneById(quizId: Long) = repository.findOneById(quizId) ?: throw QuizNotFoundException()

    override fun findOneByScpNumber(scpNumber: String) = repository.findOneByScpNumber(scpNumber)

    override fun findOneByImageUrl(imageUrl: String) = repository.findOneByImageUrl(imageUrl)

    override fun save(question: Quiz): QuizDto = repository.save(question).toDto(usersRepository)

    override fun deleteById(id: Long): Boolean {
        repository.deleteById(id)
        quizTransactionRepository.deleteAllTransactionsByQuizId(id)
        return true
    }

    override fun getFullCompleteLevelsQuizIds(userId: Long): List<Long> {
        val gameProgressTransactionTypes = listOf(
                ScpQuizConstants.QuizTransactionType.NAME_WITH_PRICE,
                ScpQuizConstants.QuizTransactionType.NAME_NO_PRICE,
                ScpQuizConstants.QuizTransactionType.NUMBER_WITH_PRICE,
                ScpQuizConstants.QuizTransactionType.NUMBER_NO_PRICE,
                ScpQuizConstants.QuizTransactionType.NAME_ENTERED_MIGRATION,
                ScpQuizConstants.QuizTransactionType.NUMBER_ENTERED_MIGRATION
        )

        val gameProgressQuizIds = quizTransactionService.getQuizIdsFromTransactionsByUserIdAndTypes(userId, gameProgressTransactionTypes)
        val foundIds = HashSet<Long>()
        val duplicatesIds = HashSet<Long>()
        for (id in gameProgressQuizIds) {
            if (foundIds.contains(id)) {
                duplicatesIds.add(id)
            } else {
                foundIds.add(id)
            }
        }
        return duplicatesIds.toList()
    }

    override fun findAllIds(): List<Long> = repository.findAllIds()

    override fun findAllByQuizTranslationsLangCode(langCode: String) =
            repository.findAllByQuizTranslationsLangCode(langCode)

    override fun findOneByQuizTranslationsId(quizTranslationId: Long) =
            repository.findOneByQuizTranslationsId(quizTranslationId) ?: throw QuizNotFoundException()

    override fun deleteAuthorIdForUserId(userId: Long) =
            repository.deleteAuthorIdForUserId(userId)
}