package ru.scp.quiz.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class EmailConfiguration {

    @Value("\${spring.mail.host}")
    lateinit var springMailHost: String

    @Value("\${spring.mail.port}")
    var springMailPort: Int? = null

    @Value("\${spring.mail.username}")
    lateinit var springMailUsername: String

    @Value("\${spring.mail.password}")
    lateinit var springMailPassword: String

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    lateinit var springMailSmtpAuth: String

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    lateinit var springMailSmtpStarttlsEnable: String

    @Bean
    fun getJavaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = springMailHost
        mailSender.port = springMailPort!!

        mailSender.username = springMailUsername
        mailSender.password = springMailPassword

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = MAIL_TRANSPORT_PROTOCOL
        props["mail.smtp.auth"] = springMailSmtpAuth
        props["mail.smtp.starttls.enable"] = springMailSmtpStarttlsEnable
        props["mail.debug"] = "true"

        return mailSender
    }

    companion object {
        const val MAIL_TRANSPORT_PROTOCOL = "smtp"
    }
}