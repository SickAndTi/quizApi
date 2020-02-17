package ru.scp.quiz.repository.inAppPurchase

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.scp.quiz.bean.inAppPurchase.InAppPurchase

@Repository
interface InAppPurchaseRepository : JpaRepository<InAppPurchase, Long> {

    fun findOneById(id: Long): InAppPurchase?

    fun findOneByPurchaseToken(purchaseToken: String): InAppPurchase?

}