package ru.scp.quiz.service.leaderboard

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import ru.scp.quiz.model.dto.LeaderboardDto
import ru.scp.quiz.model.dto.UserDto

interface LeaderboardService {

    fun getFullLeaderboard(): List<LeaderboardDto>

    fun getPartLeaderboard(offset: Int, limit: Int): List<LeaderboardDto>

    fun getUserPositionInLeaderboard(userId: Long): Int

    fun getUserForLeaderboard(userId: Long): LeaderboardDto

}

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Leaderboard not found")
class LeaderboardNotFoundException : RuntimeException()