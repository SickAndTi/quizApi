package ru.scp.quiz.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.scp.quiz.service.auth.AuthorityService
import ru.scp.quiz.service.auth.ClientService
import java.io.File


@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
class IndexController {

    @Autowired
    lateinit var authoritiesService: AuthorityService

    @Autowired
    lateinit var clientService: ClientService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @GetMapping("/")
    fun index(): String = "Greetings from Spring Boot!"

    @GetMapping("/hello")
    fun test(@RequestParam(value = "name", defaultValue = "World") name: String) = "Hello, $name"

    @GetMapping("/showClients")
    fun showClients() = clientService.findAll()

    @GetMapping("/showAuthorities")
    fun showAuthorities() = authoritiesService.findAll()

    @GetMapping("/encrypt")
    fun encrypt(@RequestParam(value = "name", defaultValue = "World") name: String): String =
            passwordEncoder.encode(name)

    @Value("\${spring.datasource.username}")
    lateinit var dbUserName: String

    @Value("\${my.db.name}")
    lateinit var database: String

    @Value("\${postgres.bin.dir}")
    lateinit var postgresBinDir: String

    @GetMapping("backupList")
    fun backUpFilesList(): String {
        val files = File("/data/dbBackup").absoluteFile
                .list { _, name -> name.startsWith(database) }

        return files.joinToString(separator = "</br>")
    }

    @GetMapping("/restoreFromBackUp")
    fun restoreFromBackUp(@RequestParam(value = "fileName") fileName: String): String {
        val formattedFileName = if (fileName.endsWith(".sql")) fileName else "$fileName.sql"
        val executeCmd = "$postgresBinDir/psql|||--username=$dbUserName|||--file=$formattedFileName|||$database"

        val runtimeProcess: Process
        try {
            val pb = ProcessBuilder(executeCmd.split("|||"))
            val logFile = File("logs/myLogPostgres.log")
            if (!logFile.exists()) {
                logFile.createNewFile()
            }
            pb.redirectErrorStream(true)
            pb.redirectOutput(logFile)
            runtimeProcess = pb.start()
            val processComplete = runtimeProcess.waitFor()

            if (processComplete == 0) {
                println("Backup restored successfully")
            } else {
                println("Could not restore the backup")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return "{status:\"done\"}"
    }
}