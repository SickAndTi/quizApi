package ru.scp.quiz.controller.transaction

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.auth.AuthorityType
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.bean.transaction.QuizTransaction
import ru.scp.quiz.bean.transaction.TransactionAlreadyExistsException
import ru.scp.quiz.controller.AccessDeniedException
import ru.scp.quiz.service.auth.UserService
import ru.scp.quiz.service.transaction.QuizTransactionService

@RestController
@RequestMapping("/${ScpQuizConstants.Path.TRANSACTIONS}")
class QuizTransactionController {

    @Autowired
    lateinit var quizTransactionService: QuizTransactionService

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/{id}")
    fun getTransactionById(@PathVariable(value = "id") id: Long) = quizTransactionService.findOneById(id)

    @GetMapping("/all")
    fun showAllTransactions() = quizTransactionService.findAll()

    @GetMapping("/allByUserId")
    fun getAllByUserId(
            @AuthenticationPrincipal user: User
    ): List<QuizTransaction> =
            quizTransactionService.findAllByUserId(user.id!!)

    @PostMapping("/add")
    fun addTransaction(
            @RequestParam(value = "quizId") quizId: Long?,
            @RequestParam(value = "typeTransaction") typeTransaction: ScpQuizConstants.QuizTransactionType,
            @RequestParam(value = "coinsAmount") coinsAmount: Int?,
            @RequestParam(value = "createdOnClient") createdOnClient: Long,
            @AuthenticationPrincipal user: User
    ): QuizTransaction {
        val quizTransaction = QuizTransaction(
                userId = user.id!!,
                quizId = quizId,
                quizTransactionType = typeTransaction,
                coinsAmount = coinsAmount,
                createdOnClient = createdOnClient
        )
        // проверяем на сходство пришедшей транзакции с уже имеющимися по quizId,userId,typeTransaction
        val quizTransactionFromDb = quizTransactionService.getTransactionByUserIdQuizIdAndType(user.id, quizId, typeTransaction)
        if (quizId == quizTransactionFromDb?.quizId && user.id == quizTransactionFromDb?.userId && typeTransaction == quizTransactionFromDb.quizTransactionType) {
            return quizTransactionFromDb
            //если такая уже существует то возвращаем её
        } else {
            if (coinsAmount != null) {
                val syncTransactionFromBd = quizTransactionService.getTransactionByUserIdAndType(user.id, ScpQuizConstants.QuizTransactionType.UPDATE_SYNC)
                //проверяем на существование транзакции синхронизации очков для этого userId
                if (typeTransaction == ScpQuizConstants.QuizTransactionType.UPDATE_SYNC && quizTransactionService.getTransactionsCountByUserId(user.id, ScpQuizConstants.QuizTransactionType.UPDATE_SYNC) > 0) {
                    if (coinsAmount > syncTransactionFromBd?.coinsAmount!!) {
                        userService.update(userService.getById(user.id).apply { score += Math.abs(coinsAmount - syncTransactionFromBd.coinsAmount) })
                        //если такая существует и пришедшие очки больше существующих то прибавляем их разницу к существующим
                    } else {
                        return syncTransactionFromBd
                        //если такая существует и пришедшие очки не больше существующих то отдаём транзакцию из БД
                    }
                } else {
                    userService.update(userService.getById(user.id).apply { score += coinsAmount })
                    //если не существует то пишем в БД и прибавляем пришедшие очки
                }
            }
            val isDisableAdsTransaction = (typeTransaction == ScpQuizConstants.QuizTransactionType.ADV_BUY_NEVER_SHOW)
            val isNotFirstDisableAdsTransaction = quizTransactionService.getTransactionsCountByUserId(
                    userId = user.id, typeTransaction = ScpQuizConstants.QuizTransactionType.ADV_BUY_NEVER_SHOW) > 0
            return if (isDisableAdsTransaction && isNotFirstDisableAdsTransaction) {
                throw TransactionAlreadyExistsException()
            } else {
                quizTransactionService.save(quizTransaction)
                // во всех остальных случаях просто пишеи в БД и отдаём
            }
        }
    }

    @DeleteMapping("/delete/{id}")
    fun deleteQuizTransaction(
            @PathVariable(value = "id") id: Long,
            @AuthenticationPrincipal user: User
    ): Boolean {
        //check if user is ADMIN or author of deleting object
        val isAdmin = user.userAuthorities.any { it.authority == AuthorityType.ADMIN.name }
        val quizTransaction = quizTransactionService.findOneById(id)
        if (!isAdmin) {
            if (quizTransaction?.userId != user.id) {
                throw AccessDeniedException()
            }
        }
        return quizTransactionService.deleteById(id)
    }

    @DeleteMapping("/deleteAll")
    fun deleteAllQuizTransactionsByUserId(
            @AuthenticationPrincipal user: User
    ): Boolean {
        userService.update(userService.getById(user.id!!).apply { score = 0 })
        quizTransactionService.deleteAllByUserId(user.id)
        return true
    }

    @GetMapping("/resetProgress")
    fun resetProgress(
            @AuthenticationPrincipal user: User
    ): Int {
        val types = listOf(
                ScpQuizConstants.QuizTransactionType.NAME_WITH_PRICE,
                ScpQuizConstants.QuizTransactionType.NAME_NO_PRICE,
                ScpQuizConstants.QuizTransactionType.NAME_CHARS_REMOVED,
                ScpQuizConstants.QuizTransactionType.NUMBER_WITH_PRICE,
                ScpQuizConstants.QuizTransactionType.NUMBER_NO_PRICE,
                ScpQuizConstants.QuizTransactionType.NUMBER_CHARS_REMOVED,
                ScpQuizConstants.QuizTransactionType.LEVEL_ENABLE_FOR_COINS,
                ScpQuizConstants.QuizTransactionType.NAME_ENTERED_MIGRATION,
                ScpQuizConstants.QuizTransactionType.NUMBER_ENTERED_MIGRATION,
                ScpQuizConstants.QuizTransactionType.NAME_CHARS_REMOVED_MIGRATION,
                ScpQuizConstants.QuizTransactionType.NUMBER_CHARS_REMOVED_MIGRATION,
                ScpQuizConstants.QuizTransactionType.LEVEL_AVAILABLE_MIGRATION
        )
        val transactionsWhenResetProgress = quizTransactionService.getTransactionsByUserIdAndTypes(user.id!!, types)
        val userFromClient = userService.getById(user.id)

        transactionsWhenResetProgress.forEach { quizTransactionReset: QuizTransaction ->
            if (quizTransactionReset.quizTransactionType == ScpQuizConstants.QuizTransactionType.NAME_WITH_PRICE) {
                userService.update(userFromClient.apply { score += ScpQuizConstants.COINS_NAME_WITH_PRICE })
            }
            if (quizTransactionReset.quizTransactionType == ScpQuizConstants.QuizTransactionType.NAME_NO_PRICE) {
                userService.update(userFromClient.apply { score += ScpQuizConstants.COINS_NAME_NO_PRICE })
            }
            if (quizTransactionReset.quizTransactionType == ScpQuizConstants.QuizTransactionType.NAME_CHARS_REMOVED) {
                userService.update(userFromClient.apply { score += ScpQuizConstants.COINS_NAME_CHARS_REMOVED })
            }
            if (quizTransactionReset.quizTransactionType == ScpQuizConstants.QuizTransactionType.NUMBER_WITH_PRICE) {
                userService.update(userFromClient.apply { score += ScpQuizConstants.COINS_NUMBER_WITH_PRICE })
            }
            if (quizTransactionReset.quizTransactionType == ScpQuizConstants.QuizTransactionType.NUMBER_NO_PRICE) {
                userService.update(userFromClient.apply { score += ScpQuizConstants.COINS_NUMBER_NO_PRICE })
            }
            if (quizTransactionReset.quizTransactionType == ScpQuizConstants.QuizTransactionType.NUMBER_CHARS_REMOVED) {
                userService.update(userFromClient.apply { score += ScpQuizConstants.COINS_NUMBER_CHARS_REMOVED })
            }
            if (quizTransactionReset.quizTransactionType == ScpQuizConstants.QuizTransactionType.LEVEL_ENABLE_FOR_COINS) {
                userService.update(userFromClient.apply { score += ScpQuizConstants.COINS_LEVEL_ENABLE_FOR_COINS })
            }
        }
        quizTransactionService.deleteTransactionsByUserIdAndTypes(user.id, types)

        return userService.getById(user.id).score.toInt()
    }
}