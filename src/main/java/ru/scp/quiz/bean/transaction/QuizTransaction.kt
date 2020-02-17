package ru.scp.quiz.bean.transaction

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.ScpQuizConstants
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "quiz_transaction")
data class QuizTransaction(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        //content
        @Column(name = "user_id")
        var userId: Long,
        @Column(name = "quiz_id")
        var quizId: Long? = null,
        @Column(name = "transaction_type")
        val quizTransactionType: ScpQuizConstants.QuizTransactionType,
        @Column(name = "coins_amount")
        val coinsAmount: Int? = null,
        //dates
        @field:CreationTimestamp
        val created: Timestamp? = null,
        @field:UpdateTimestamp
        val updated: Timestamp? = null,
        @Column(name = "created_on_client")
        val createdOnClient: Long

)

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "QuizTransaction not found")
class TransactionNotFoundException : RuntimeException()

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Transaction already exists")
class TransactionAlreadyExistsException : RuntimeException()
