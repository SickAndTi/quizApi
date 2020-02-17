package ru.scp.quiz.controller

import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.bean.auth.Authority
import ru.scp.quiz.bean.auth.AuthorityType
import ru.scp.quiz.bean.auth.User
import ru.scp.quiz.bean.auth.UserAlreadyExistsException
import ru.scp.quiz.network.ApiClient
import ru.scp.quiz.repository.auth.UserNotFoundException
import ru.scp.quiz.service.auth.AuthorityService
import ru.scp.quiz.service.auth.EmailService
import ru.scp.quiz.service.auth.UserService
import java.io.Serializable
import java.util.*
import javax.security.auth.message.AuthException

@RestController
@RequestMapping("/${ScpQuizConstants.Path.AUTH}")
class AuthController {

    @Autowired
    private lateinit var log: Logger

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var passwordGenerator: RandomValueStringGenerator

    @Autowired
    private lateinit var clientDetailsService: ClientDetailsService

    @Autowired
    private lateinit var authorityService: AuthorityService

    @Autowired
    private lateinit var usersService: UserService

    @Autowired
    private lateinit var tokenStore: TokenStore

    @Autowired
    private lateinit var tokenServices: DefaultTokenServices

    @Autowired
    private lateinit var emailService: EmailService

    @Autowired
    private lateinit var apiClient: ApiClient

    @PostMapping("registration")
    fun register(
            @RequestParam(value = "email") email: String,
            @RequestParam(value = "password") password: String,
            @RequestParam(value = "fullName") fullName: String,
            @RequestParam(value = "avatarUrl") avatarUrl: String? = null,
            @RequestParam(value = "clientId") clientId: String,
            @RequestParam(value = "clientSecret") clientSecret: String
    ): OAuth2AccessToken {
        //check if user already exists
        val userInDb = usersService.getByUsername(email)
        if (userInDb != null) {
            throw UserAlreadyExistsException()
        }
        val newUserInDb = usersService.insert(User(
                myUsername = email,
                myPassword = passwordEncoder.encode(password),
                avatar = avatarUrl,
                userAuthorities = setOf(),
                fullName = fullName
        ))

        authorityService.insert(Authority(newUserInDb.id!!, AuthorityType.USER.name))

        emailService.sendEmail(email, REGISTRATION_EMAIL_SUBJECT, "Your password is:\n$password")

        return getAccessToken(email, clientId)
    }

    @PostMapping("/socialLogin")
    fun authorize(
            @RequestParam(value = "provider") provider: ScpQuizConstants.SocialProvider,
            @RequestParam(value = "token") token: String,
            @RequestParam(value = "clientId") clientId: String,
            @RequestParam(value = "clientSecret") clientSecret: String,
            @RequestParam(value = "clientApp") clientApp: ScpQuizConstants.ClientApp
    ): OAuth2AccessToken? {
        println("authorize called")

        val clientDetails: ClientDetails = clientDetailsService.loadClientByClientId(clientId)
        println("clientSecret: $clientSecret")
        println("clientDetails.clientSecret: ${clientDetails.clientSecret}")
        if (clientDetails.clientSecret == passwordEncoder.encode(clientSecret)) {
            throw IllegalArgumentException("Wrong clientSecret! Check your settings.")
        }

        val commonUserData = apiClient.getUserDataFromProvider(provider, token, clientApp)

        val email = commonUserData.email ?: throw AuthException("no email found!")

        var userInDb = usersService.getByUsername(email)
        if (userInDb != null) {
            //add provider id to user object if need
            when (provider) {
                ScpQuizConstants.SocialProvider.GOOGLE -> {
                    if (userInDb.googleId.isNullOrEmpty()) {
                        userInDb.googleId = commonUserData.id
                        usersService.update(userInDb)

                    } else if (userInDb.googleId != commonUserData.id) {
                        log.error("login with ${commonUserData.id}/$email for user with mismatched googleId: ${userInDb.googleId}")
                    }
                }
                ScpQuizConstants.SocialProvider.FACEBOOK -> {
                    if (userInDb.facebookId.isNullOrEmpty()) {
                        userInDb.facebookId = commonUserData.id
                        usersService.update(userInDb)

                    } else if (userInDb.facebookId != commonUserData.id) {
                        log.error("login with ${commonUserData.id}/$email for user with mismatched facebookId: ${userInDb.facebookId}")
                    }
                }
                ScpQuizConstants.SocialProvider.VK -> {
                    if (userInDb.vkId.isNullOrEmpty()) {
                        userInDb.vkId = commonUserData.id
                        usersService.update(userInDb)

                    } else if (userInDb.vkId != commonUserData.id) {
                        log.error("login with ${commonUserData.id}/$email for user with mismatched vkId: ${userInDb.vkId}")
                    }
                }
            }
            revokeUserTokens(email, clientId)
            return getAccessToken(email, clientId)
        } else {
            //try to find by providers id as email may be changed
            userInDb = usersService.getByProviderId(commonUserData.id!!, provider)
            if (userInDb != null) {
                return getAccessToken(userInDb.username, clientId)
            } else {
                //if cant find - register new user
                val password = passwordGenerator.generate()
                println("password: $email/$password")
                val newUserInDb = usersService.insert(User(
                        myUsername = email,
                        myPassword = passwordEncoder.encode(password),
                        avatar = commonUserData.avatarUrl,
                        userAuthorities = setOf(),
                        fullName = commonUserData.fullName
                ).apply {
                    when (provider) {
                        ScpQuizConstants.SocialProvider.GOOGLE -> googleId = commonUserData.id
                        ScpQuizConstants.SocialProvider.FACEBOOK -> facebookId = commonUserData.id
                        ScpQuizConstants.SocialProvider.VK -> vkId = commonUserData.id
                    }
                })
                val authority = Authority(newUserInDb.id!!, AuthorityType.USER.name)
                authorityService.insert(authority)

                emailService.sendEmail(email, REGISTRATION_EMAIL_SUBJECT, "Your password is:\n$password")

                return getAccessToken(newUserInDb.username, clientId)
            }
        }
    }

    private fun revokeUserTokens(email: String, clientId: String) =
            tokenStore.findTokensByClientIdAndUserName(clientId, email).forEach {
                tokenServices.revokeToken(it.value)
            }


    fun getAccessToken(email: String, clientId: String): OAuth2AccessToken {
        val clientDetails: ClientDetails = clientDetailsService.loadClientByClientId(clientId)

        val requestParameters = mapOf<String, String>()
        val authorities: MutableCollection<GrantedAuthority> = clientDetails.authorities
        val approved = true
        val scope: MutableSet<String> = clientDetails.scope
        val resourceIds: MutableSet<String> = clientDetails.resourceIds
        val redirectUri = null
        val responseTypes = setOf("code")
        val extensionProperties = HashMap<String, Serializable>()

        val oAuth2Request = OAuth2Request(
                requestParameters,
                clientId,
                authorities,
                approved,
                scope,
                resourceIds,
                redirectUri,
                responseTypes,
                extensionProperties
        )

        val user = usersService.loadUserByUsername(email) ?: throw UserNotFoundException()
        val authenticationToken = UsernamePasswordAuthenticationToken(
                user,
                user.password,
                authorities
        )

        val auth = OAuth2Authentication(oAuth2Request, authenticationToken)

        return tokenServices.createAccessToken(auth)
    }

    companion object {
        const val REGISTRATION_EMAIL_SUBJECT = "Welcome to Scp Quiz!"
    }
}
