package ru.scp.quiz.controller.inAppPurchase

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.bean.inAppPurchase.InAppPurchase
import ru.scp.quiz.bean.transaction.QuizTransaction
import ru.scp.quiz.service.auth.UserService
import ru.scp.quiz.service.inAppPurchase.InAppPurchaseService
import ru.scp.quiz.service.transaction.QuizTransactionService


@RestController
@RequestMapping("/${ScpQuizConstants.Path.IN_APP_PURCHASE}")
class InAppPurchaseController {

    @Autowired
    lateinit var inAppPurchaseService: InAppPurchaseService

    @Autowired
    lateinit var transactionService: QuizTransactionService

    @Autowired
    lateinit var userService: UserService

    @PostMapping("/add")
    fun addInAppPurchase(
            @RequestParam(value = "skuId") skuId: String,
            @RequestParam(value = "purchaseTime") purchaseTime: Long,
            @RequestParam(value = "purchaseToken") purchaseToken: String,
            @RequestParam(value = "orderId") orderId: String,
            @RequestParam(value = "coinsAmount") coinsAmount: Int,
            @AuthenticationPrincipal user: User
    ): QuizTransaction {

        val transactionToSave = QuizTransaction(
                quizId = null,
                quizTransactionType = ScpQuizConstants.QuizTransactionType.INAPP_PURCHASE,
                coinsAmount = coinsAmount,
                userId = user.id!!,
                createdOnClient = System.currentTimeMillis()
        )

        val inAppPurchaseByToken = inAppPurchaseService.findOneByPurchaseToken(purchaseToken)

        if (inAppPurchaseByToken != null) {
            val transactionByPurchaseToken = transactionService.findOneById(inAppPurchaseByToken.transactionId)
            if (transactionByPurchaseToken != null) {
                return transactionByPurchaseToken
            } else {
                val savedTransaction = transactionService.save(transactionToSave)
                userService.update(userService.getById(user.id).apply { score += coinsAmount })
                return savedTransaction
            }
        } else {
            val savedTransaction = transactionService.save(transactionToSave)
            userService.update(userService.getById(user.id).apply { score += coinsAmount })

            val inAppPurchase = InAppPurchase(
                    transactionId = savedTransaction.id!!,
                    skuId = skuId,
                    purchaseTime = purchaseTime,
                    purchaseToken = purchaseToken,
                    orderId = orderId
            )
            inAppPurchaseService.save(inAppPurchase)

            return savedTransaction
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addGift")
    fun addInAppPurchaseGift(
            @RequestParam(value = "coinsAmount") coinsAmount: Int,
            @RequestParam(value = "userId") userId: Long,
            @AuthenticationPrincipal user: User
    ): QuizTransaction {

        val transactionToSave = QuizTransaction(
                quizId = null,
                quizTransactionType = ScpQuizConstants.QuizTransactionType.INAPP_PURCHASE_GIFT,
                coinsAmount = coinsAmount,
                userId = userId,
                createdOnClient = System.currentTimeMillis()
        )
        val savedTransaction = transactionService.save(transactionToSave)
        userService.update(userService.getById(userId).apply { score += coinsAmount })

        val inAppPurchase = InAppPurchase(
                transactionId = savedTransaction.id!!,
                skuId = null,
                purchaseTime = System.currentTimeMillis(),
                purchaseToken = ScpQuizConstants.DEFAULT_PURCHASE_GIFT_TOKEN,
                orderId = ScpQuizConstants.DEFAULT_PURCHASE_ORDER_ID
        )

        inAppPurchaseService.save(inAppPurchase)
        return savedTransaction
    }
}