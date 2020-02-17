package ru.scp.quiz.model.facebook

data class ValidatedTokenWrapper(
        val verifiedToken: DebugTokenResponse?,
        val exception: Throwable?
)