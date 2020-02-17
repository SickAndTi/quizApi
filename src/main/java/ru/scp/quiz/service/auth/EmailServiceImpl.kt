package ru.scp.quiz.service.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl : EmailService {

    @Autowired
    private lateinit var emailSender: JavaMailSender;

    override fun sendEmail(to: String, subject: String, text: String) {
        val message = SimpleMailMessage();
        message.setTo(to);
        message.subject = subject;
        message.text = text;
        emailSender.send(message);
    }
}