package ru.scp.quiz.bean.auth

import ru.scp.quiz.utils.NoArgConstructor
import java.io.Serializable

@NoArgConstructor
data class KeyUserAuthority(
        var userId: Long? = null,
        var authority: String? = null
) : Serializable