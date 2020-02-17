package ru.scp.quiz.model.dto

import java.sql.Timestamp

data class QuizTranslationPhraseDto(
        //db
        val id: Long? = null,
        //content
        val translation: String,
        //status
        val approved: Boolean = false,
        var authorId: Long?,
        val approverId: Long? = null,
        //dates
        val created: Timestamp? = null,
        val updated: Timestamp? = null,
        //users
        val author: UserDto?,
        val approver: UserDto?
)