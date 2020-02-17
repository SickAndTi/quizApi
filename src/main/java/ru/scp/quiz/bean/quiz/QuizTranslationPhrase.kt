package ru.scp.quiz.bean.quiz

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import ru.scp.quiz.model.dto.QuizTranslationPhraseDto
import ru.scp.quiz.repository.auth.UsersRepository
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "quiz_translation_phrases")
data class QuizTranslationPhrase(
        //db
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @ManyToOne
        val quizTranslation: QuizTranslation? = null,
        //content
        val translation: String,
        //status
        var approved: Boolean = false,
        @Column(name = "author_id")
        var authorId: Long?,
        @Column(name = "approver_id")
        var approverId: Long? = null,
        //dates
        @field:CreationTimestamp
        val created: Timestamp? = null,
        @field:UpdateTimestamp
        val updated: Timestamp? = null
)

fun QuizTranslationPhrase.toDto(usersRepository: UsersRepository) =
        QuizTranslationPhraseDto(
                id = id,
                translation = translation,
                authorId = authorId,
                author = authorId?.let { usersRepository.getOneAsUserDto(it) },
                approverId = approverId,
                approver = approverId?.let { usersRepository.getOneAsUserDto(it) },
                approved = approved,
                created = created,
                updated = updated
        )