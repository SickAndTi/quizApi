package ru.scp.quiz.service.leaderboard

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.model.dto.LeaderboardDto
import ru.scp.quiz.repository.auth.UsersRepository
import ru.scp.quiz.repository.transaction.QuizTransactionRepository
import java.util.*


@Service
class LeaderboardServiceImpl : LeaderboardService {
    @Autowired
    private lateinit var userRepository: UsersRepository

    @Autowired
    private lateinit var transactionRepository: QuizTransactionRepository

    override fun getFullLeaderboard(): List<LeaderboardDto> = userRepository.getFullLeaderboard().map { it.toLeaderboardDto() }

    override fun getPartLeaderboard(offset: Int, limit: Int): List<LeaderboardDto> = userRepository.getPartLeaderboard(offset, limit).map { it.toLeaderboardDto() }

    override fun getUserPositionInLeaderboard(userId: Long): Int = userRepository.getUserPositionInLeaderboard(userId)

    val gameProgressTransactionTypes = listOf(
            ScpQuizConstants.QuizTransactionType.NAME_WITH_PRICE.ordinal.toString(),
            ScpQuizConstants.QuizTransactionType.NAME_NO_PRICE.ordinal.toString(),
            ScpQuizConstants.QuizTransactionType.NUMBER_WITH_PRICE.ordinal.toString(),
            ScpQuizConstants.QuizTransactionType.NUMBER_NO_PRICE.ordinal.toString(),
            ScpQuizConstants.QuizTransactionType.NAME_ENTERED_MIGRATION.ordinal.toString(),
            ScpQuizConstants.QuizTransactionType.NUMBER_ENTERED_MIGRATION.ordinal.toString()
    )

    fun getCountFullAndPartCompleteLevels(userId: Long): Pair<Int, Int> {

        val quizIdsToCheck = transactionRepository.getQuizIdsFromTransactionsByUserIdAndTypes(userId, gameProgressTransactionTypes)
        val foundIds = HashSet<Long>()
        val duplicatesIds = HashSet<Long>()
        for (id in quizIdsToCheck) {
            if (foundIds.contains(id)) {
                duplicatesIds.add(id)
            } else {
                foundIds.add(id)
            }
        }
        return Pair(duplicatesIds.size, quizIdsToCheck.size - 2 * duplicatesIds.size)
    }

    fun User.toLeaderboardDto() = LeaderboardDto(
            id = id!!,
            avatar = avatar,
            fullName = fullName,
            score = score,
            fullCompleteLevels = getCountFullAndPartCompleteLevels(id).first,
            partCompleteLevels = getCountFullAndPartCompleteLevels(id).second
    )

    override fun getUserForLeaderboard(userId: Long): LeaderboardDto = userRepository.findOneById(userId)!!.toLeaderboardDto()
}
