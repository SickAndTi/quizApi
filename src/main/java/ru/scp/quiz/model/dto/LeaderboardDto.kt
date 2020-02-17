package ru.scp.quiz.model.dto

data class LeaderboardDto(
        val id: Long,
        val fullName: String?,
        val avatar: String?,
        val score: Long = 0,
        val fullCompleteLevels: Int = 0,
        val partCompleteLevels: Int = 0
)
