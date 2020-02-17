package ru.scp.quiz.service.transaction

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.transaction.QuizTransaction
import ru.scp.quiz.repository.transaction.QuizTransactionRepository

@Service
class QuizTransactionServiceImpl : QuizTransactionService {

    @Autowired
    private lateinit var repository: QuizTransactionRepository

    override fun findAll(): List<QuizTransaction> =
            repository.findAll()

    override fun findOneById(transactionId: Long): QuizTransaction = repository.findOneById(transactionId)

    override fun save(quizTransaction: QuizTransaction): QuizTransaction =
            repository.save(quizTransaction)

    override fun deleteById(id: Long): Boolean {
        repository.deleteById(id)
        return true
    }

    override fun findAllByUserId(userId: Long): List<QuizTransaction> =
            repository.findAllByUserId(userId)

    override fun getTransactionsCountByUserId(userId: Long, typeTransaction: ScpQuizConstants.QuizTransactionType): Int =
            repository.getTransactionsCountByUserId(userId, typeTransaction.ordinal.toString())

    override fun getTransactionByUserIdAndType(userId: Long, typeTransaction: ScpQuizConstants.QuizTransactionType): QuizTransaction? =
            repository.getTransactionByUserIdAndType(userId, typeTransaction.ordinal.toString())

    override fun getTransactionByUserIdQuizIdAndType(userId: Long, quizId: Long?, typeTransaction: ScpQuizConstants.QuizTransactionType): QuizTransaction? {
        if (quizId != null) {
            return repository.getTransactionByUserIdQuizIdAndType(userId, quizId, typeTransaction.ordinal.toString())
        } else {
            return repository.getTransactionByUserIdQuizIdAndType(userId, typeTransaction.ordinal.toString())
        }
    }

    override fun deleteAllByUserId(userId: Long) = repository.deleteAllByUserId(userId)

    override fun deleteTransactionsByUserIdAndTypes(userId: Long, types: List<ScpQuizConstants.QuizTransactionType>) =
            repository.deleteTransactionsByUserIdAndTypes(userId, types.map { it.ordinal.toString() })

    override fun getQuizIdsFromTransactionsByUserIdAndTypes(userId: Long, types: List<ScpQuizConstants.QuizTransactionType>) =
            repository.getQuizIdsFromTransactionsByUserIdAndTypes(userId, types.map { it.ordinal.toString() })

    override fun getTransactionsByUserIdAndTypes(userId: Long, types: List<ScpQuizConstants.QuizTransactionType>): List<QuizTransaction> =
            repository.getTransactionsByUserIdAndTypes(userId, types.map { it.ordinal.toString() })

}
