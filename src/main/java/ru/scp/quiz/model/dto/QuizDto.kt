package ru.scp.quiz.model.dto

import java.sql.Timestamp

data class QuizDto(
        val id: Long,
        //content
        val scpNumber: String,
        val imageUrl: String,
        val quizTranslations: Set<QuizTranslationDto>,
        //status
        var authorId: Long?,
        var approved: Boolean = false,
        var approverId: Long? = null,
        //dates
        val created: Timestamp? = null,
        val updated: Timestamp? = null,
        //users
        val author: UserDto?,
        val approver: UserDto?
)