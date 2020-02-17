package ru.scp.quiz.service.auth

interface EmailService {

    fun sendEmail(to: String, subject: String, text: String)
}