package ru.scp.quiz.controller.leaderboard

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.model.dto.LeaderboardDto
import ru.scp.quiz.service.leaderboard.LeaderboardService
import ru.scp.quiz.service.transaction.QuizTransactionService

@RestController
@RequestMapping("/${ScpQuizConstants.Path.LEADERBOARD}")
class LeaderboardController {

    @Autowired
    lateinit var leaderboardService: LeaderboardService

    @GetMapping("/getFullLeaderboard")
    fun getFullLeaderBoard(): List<LeaderboardDto> = leaderboardService.getFullLeaderboard()

    @GetMapping("/getLeaderboard")
    fun getPartLeaderboard(
            @RequestParam offset: Int,
            @RequestParam limit: Int
    ): List<LeaderboardDto> = leaderboardService.getPartLeaderboard(offset, limit)

    @GetMapping("/currentPosition")
    fun getCurrentPosition(@AuthenticationPrincipal user: User): Int = leaderboardService.getUserPositionInLeaderboard(user.id!!)

    @GetMapping("/getUserForLeaderboard")
    fun getUserForLeaderboard(@AuthenticationPrincipal user: User): LeaderboardDto = leaderboardService.getUserForLeaderboard(user.id!!)
}