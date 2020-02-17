package ru.scp.quiz.model.dto

import java.sql.Timestamp

data class QuizTranslationDto(
        //db
        val id: Long? = null,
        //content
        val langCode: String,
        val translation: String,
        val description: String,
        val quizTranslationPhrases: MutableSet<QuizTranslationPhraseDto> = mutableSetOf(),
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