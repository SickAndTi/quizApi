package ru.scp.quiz.bean.inAppPurchase

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import javax.persistence.*

@Entity
@Table(name = "quiz_exam_inapp_purchase")
data class InAppPurchase(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        //content
        @Column(name = "transaction_id")
        val transactionId: Long,
        @Column(name = "sku_id")
        val skuId: String? = null,
        @Column(name = "purchase_time")
        val purchaseTime: Long,
        @Column(name = "purchase_token")
        val purchaseToken: String? = null,
        @Column(name = "order_id")
        val orderId: String
)

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "InAppPurchase not found")
class InAppPurchaseNotFoundException : RuntimeException()

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "InAppPurchase already exists")
class InAppPurchaseAlreadyExistsException : RuntimeException()
