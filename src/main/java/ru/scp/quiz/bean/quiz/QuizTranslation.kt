package ru.scp.quiz.bean.quiz

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import ru.scp.quiz.model.dto.QuizTranslationDto
import ru.scp.quiz.repository.auth.UsersRepository
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "quiz_translations")
data class QuizTranslation(
        //db
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        @ManyToOne
        var quiz: Quiz? = null,
        //content
        @Column(name = "lang_code")
        val langCode: String,
        val translation: String,
        val description: String,
        @OneToMany(
                cascade = [CascadeType.ALL],
                fetch = FetchType.EAGER,
                orphanRemoval = true
        )
        @JoinColumn(name = "quizTranslation")
        val quizTranslationPhrases: MutableSet<QuizTranslationPhrase> = mutableSetOf(),
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

fun QuizTranslation.toDto(usersRepository: UsersRepository) =
        QuizTranslationDto(
                id = id,
                translation = translation,
                langCode = langCode,
                description = description,
                quizTranslationPhrases = quizTranslationPhrases.map { it.toDto(usersRepository) }.toHashSet(),
                //common
                authorId = authorId,
                author = authorId?.let { usersRepository.getOneAsUserDto(it) },
                approverId = approverId,
                approver = approverId?.let { usersRepository.getOneAsUserDto(it) },
                approved = approved,
                created = created,
                updated = updated
        )