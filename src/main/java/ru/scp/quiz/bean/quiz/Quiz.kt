package ru.scp.quiz.bean.quiz

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import ru.scp.quiz.model.dto.QuizDto
import ru.scp.quiz.repository.auth.UsersRepository
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "quiz")
data class Quiz(
        //db
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        //content
        @Column(name = "scp_number", unique = true)
        val scpNumber: String,
        @Column(name = "image_url", unique = true)
        val imageUrl: String,
        @OneToMany(
                cascade = [CascadeType.ALL],
                fetch = FetchType.EAGER,
                orphanRemoval = true
        )
        @JoinColumn(name = "quiz")
        val quizTranslations: MutableSet<QuizTranslation> = mutableSetOf(),
        //status
        @Column(name = "author_id")
        var authorId: Long?,
        var approved: Boolean = false,
        @Column(name = "approver_id")
        var approverId: Long? = null,
        //dates
        @field:CreationTimestamp
        val created: Timestamp? = null,
        @field:UpdateTimestamp
        val updated: Timestamp? = null
)

fun Quiz.toDto(usersRepository: UsersRepository) = QuizDto(
        id = id!!,
        scpNumber = scpNumber,
        imageUrl = imageUrl,
        quizTranslations = quizTranslations.asSequence().map { it.toDto(usersRepository) }.toHashSet(),
        //common
        authorId = authorId,
        author = authorId?.let { usersRepository.getOneAsUserDto(it) },
        approverId = approverId,
        approver = approverId?.let { usersRepository.getOneAsUserDto(it) },
        approved = approved,
        created = created,
        updated = updated
)