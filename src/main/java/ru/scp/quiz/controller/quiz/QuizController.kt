package ru.scp.quiz.controller.quiz

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.scp.quiz.ScpQuizConstants.Path.QUIZ
import ru.scp.quiz.bean.auth.AuthorityType
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.bean.quiz.Quiz
import ru.scp.quiz.controller.AccessDeniedException
import ru.scp.quiz.model.dto.QuizDto
import ru.scp.quiz.service.quiz.QuizAlreadyExistsException
import ru.scp.quiz.service.quiz.QuizService
import ru.scp.quiz.service.quiz.QuizTranslationAlreadyExistsException
import ru.scp.quiz.service.quiz.QuizTranslationService


@RestController
@RequestMapping("/$QUIZ")
class QuizController {

    @Autowired
    lateinit var quizService: QuizService

    @Autowired
    lateinit var quizTranslationService: QuizTranslationService

    @GetMapping("/{id}")
    fun getQuizById(@PathVariable(value = "id") id: Long) = quizService.findOneById(id)

    @GetMapping("/getQuizByQuizTranslationId")
    fun getQuizByQuizTranslationId(@RequestParam(value = "quizTranslationId") quizTranslationId: Long) =
            quizService.findOneByQuizTranslationsId(quizTranslationId)

    @GetMapping("/getByLangCode")
    fun getQuizByLangCode(@RequestParam(value = "langCode") langCode: String) =
            quizService.findAllByQuizTranslationsLangCode(langCode)

    @Suppress("unused")
    @DeleteMapping("/delete/{id}")
    fun deleteQuiz(
            @PathVariable(value = "id") id: Long,
            @AuthenticationPrincipal user: User
    ): Boolean {
        //check if user is ADMIN or author of deleting object
        val isAdmin = user.userAuthorities.any { it.authority == AuthorityType.ADMIN.name }
        val quiz = quizService.findOneById(id)
        if (!isAdmin) {
            if (quiz.authorId != user.id) {
                throw AccessDeniedException()
            }
        }
        return quizService.deleteById(id)
    }

    @GetMapping("/all")
    fun showAllQuizes() = quizService.findAll()

    @GetMapping("/allWithUsers")
    fun showAllQuizzesWithUsers(
            @RequestParam offset: Int,
            @RequestParam limit: Int,
            @AuthenticationPrincipal user: User
    ): List<QuizDto> {
        val isAdmin = user.userAuthorities.any { it.authority == AuthorityType.ADMIN.name }

        return if (isAdmin) {
            val getAllQuizIds: List<Long> = quizService.findAllIds()
            quizService.getQuizzesForPaging(offset, limit, getAllQuizIds)
        } else {
            val getFullCompleteLevelsQuizIds = quizService.getFullCompleteLevelsQuizIds(user.id!!)
            if (getFullCompleteLevelsQuizIds.isNotEmpty()) {
                quizService.getQuizzesForPaging(offset, limit, getFullCompleteLevelsQuizIds)
            } else {
                emptyList()
            }
        }
    }

    @GetMapping("/allSorted")
    fun showAllQuizesSorted(
            @RequestParam(value = "sortFieldName") sortFieldName: String,
            @RequestParam(value = "ascending") ascending: Boolean
    ) = quizService.findAllSorted(sortFieldName, ascending)

    @PostMapping("/create")
    fun createQuiz(
            @RequestBody quiz: Quiz,
            @AuthenticationPrincipal user: User
    ): QuizDto {
        if (quiz.scpNumber.isEmpty()) {
            throw IllegalArgumentException("Empty scpNumber!")
        }
        if (quiz.imageUrl.isEmpty()) {
            throw IllegalArgumentException("Empty imageUrl!")
        }
        var quizInDb = quizService.findOneByScpNumber(quiz.scpNumber)
        if (quizInDb != null) {
            throw QuizAlreadyExistsException("There is already quiz with such scpNumber!")
        }
        quizInDb = quizService.findOneByImageUrl(quiz.imageUrl)
        if (quizInDb != null) {
            throw QuizAlreadyExistsException("There is already quiz with such imageUrl!")
        }
        //check if it's exiting already
        quiz.quizTranslations.forEach {
            if (quizTranslationService.findOneByTextAndLangCode(it.translation, it.langCode) != null) {
                throw QuizTranslationAlreadyExistsException("Such text already exists for this langCode!")
            }
        }
        quiz.authorId = user.id!!
        return quizService.save(quiz)
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/approve")
    fun approveQuiz(
            @PathVariable(value = "id") id: Long,
            @RequestParam(value = "approve") approve: Boolean,
            @AuthenticationPrincipal user: User
    ): QuizDto {
        val quiz = quizService.findOneById(id)
        quiz.approved = approve
        quiz.approverId = user.id
        return quizService.save(quiz)
    }
}