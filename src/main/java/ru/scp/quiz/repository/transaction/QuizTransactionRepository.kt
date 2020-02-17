package ru.scp.quiz.repository.transaction


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.scp.quiz.bean.transaction.QuizTransaction

@Repository
interface QuizTransactionRepository : JpaRepository<QuizTransaction, Long> {

    fun findOneById(id: Long): QuizTransaction

    fun findAllByUserId(userId: Long): List<QuizTransaction>

    @Query("SELECT COUNT(*) FROM quiz_transaction WHERE user_id = :userId AND transaction_type = :typeTransaction", nativeQuery = true)
    fun getTransactionsCountByUserId(userId: Long, typeTransaction: String): Int

    @Query("SELECT * FROM quiz_transaction WHERE user_id = :userId AND transaction_type = :typeTransaction", nativeQuery = true)
    fun getTransactionByUserIdAndType(userId: Long, typeTransaction: String): QuizTransaction?

    @Query("SELECT * FROM quiz_transaction WHERE user_id = :userId AND quiz_id = :quizId AND transaction_type = :typeTransaction", nativeQuery = true)
    fun getTransactionByUserIdQuizIdAndType(userId: Long, quizId: Long, typeTransaction: String): QuizTransaction?

    @Query("SELECT * FROM quiz_transaction WHERE user_id = :userId AND quiz_id = NULL AND transaction_type = :typeTransaction", nativeQuery = true)
    fun getTransactionByUserIdQuizIdAndType(userId: Long, typeTransaction: String): QuizTransaction?

    @Transactional
    @Modifying
    @Query("DELETE FROM quiz_transaction WHERE user_id = :userId", nativeQuery = true)
    fun deleteAllByUserId(userId: Long)

    @Transactional
    @Modifying
    @Query("DELETE FROM quiz_transaction WHERE quiz_id = :quizId", nativeQuery = true)
    fun deleteAllTransactionsByQuizId(quizId: Long)

    @Transactional
    @Modifying
    @Query("DELETE FROM quiz_transaction WHERE user_id = :userId AND transaction_type IN :types", nativeQuery = true)
    fun deleteTransactionsByUserIdAndTypes(userId: Long, types: List<String>)

    @Transactional
    @Modifying
    @Query("SELECT * FROM quiz_transaction WHERE user_id = :userId AND transaction_type IN :types", nativeQuery = true)
    fun getTransactionsByUserIdAndTypes(userId: Long, types: List<String>): List<QuizTransaction>

    @Transactional
    @Modifying
    @Query("SELECT quiz_id FROM quiz_transaction WHERE user_id = :userId AND transaction_type IN :types", nativeQuery = true)
    fun getQuizIdsFromTransactionsByUserIdAndTypes(userId: Long, types: List<String>): List<Long>

}