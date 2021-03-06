package ru.scp.quiz

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.context.annotation.Bean


@SpringBootApplication
@EnableScheduling
class Application : SpringBootServletInitializer() {
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(Application::class.java)
    }

    @Bean
    fun logger(): Logger {
        return LoggerFactory.getLogger("application")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
