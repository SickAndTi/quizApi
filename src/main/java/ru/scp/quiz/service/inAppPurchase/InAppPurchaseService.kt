package ru.scp.quiz.service.inAppPurchase

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.bean.inAppPurchase.InAppPurchase

interface InAppPurchaseService {

    fun findOneById(id: Long): InAppPurchase?

    fun findOneByPurchaseToken(purchaseToken: String): InAppPurchase?

    fun save(inAppPurchase: InAppPurchase): InAppPurchase

    fun deleteById(id: Long): Boolean

}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "InAppPurchase not found")
class InAppPurchaseNotFoundException : RuntimeException()