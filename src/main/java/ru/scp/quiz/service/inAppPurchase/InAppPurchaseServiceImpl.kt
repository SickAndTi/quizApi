package ru.scp.quiz.service.inAppPurchase

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.scp.quiz.bean.inAppPurchase.InAppPurchase
import ru.scp.quiz.repository.inAppPurchase.InAppPurchaseRepository

@Service
class InAppPurchaseServiceImpl : InAppPurchaseService {
    @Autowired
    private lateinit var inAppPurchaseRepository: InAppPurchaseRepository

    override fun findOneById(id: Long): InAppPurchase? =
            inAppPurchaseRepository.findOneById(id)

    override fun findOneByPurchaseToken(purchaseToken: String): InAppPurchase? = inAppPurchaseRepository.findOneByPurchaseToken(purchaseToken)

    override fun save(inAppPurchase: InAppPurchase): InAppPurchase =
            inAppPurchaseRepository.save(inAppPurchase)

    override fun deleteById(id: Long): Boolean {
        inAppPurchaseRepository.deleteById(id)
        return true
    }
}