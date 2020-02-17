package ru.scp.quiz.service.transaction

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.transaction.QuizTransaction

interface QuizTransactionService {

    fun findAll(): List<QuizTransaction>

    fun findOneById(transactionId: Long): QuizTransaction?

    fun save(quizTransaction: QuizTransaction): QuizTransaction

    fun deleteById(id: Long): Boolean

    fun findAllByUserId(userId: Long): List<QuizTransaction>

    fun getTransactionsCountByUserId(userId: Long, typeTransaction: ScpQuizConstants.QuizTransactionType): Int

    fun getTransactionByUserIdAndType(userId: Long, typeTransaction: ScpQuizConstants.QuizTransactionType): QuizTransaction?

    fun getTransactionByUserIdQuizIdAndType(userId: Long, quizId: Long?, typeTransaction: ScpQuizConstants.QuizTransactionType): QuizTransaction?

    fun deleteAllByUserId(userId: Long)

    fun deleteTransactionsByUserIdAndTypes(userId: Long, types: List<ScpQuizConstants.QuizTransactionType>)

    fun getTransactionsByUserIdAndTypes(userId: Long, types: List<ScpQuizConstants.QuizTransactionType>): List<QuizTransaction>

    fun getQuizIdsFromTransactionsByUserIdAndTypes(userId: Long, types: List<ScpQuizConstants.QuizTransactionType>): List<Long>
}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "QuizTransaction not found")
class TransactionNotFoundException : RuntimeException()
