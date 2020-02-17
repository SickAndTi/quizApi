package ru.scp.quiz.network

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import retrofit2.HttpException
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.model.dto.auth.CommonUserData
import ru.scp.quiz.model.facebook.FacebookProfileResponse
import ru.scp.quiz.model.facebook.ValidatedTokenWrapper
import javax.annotation.PostConstruct

@Service
class ApiClient {

    companion object {
        const val OK_HTTP_CONNECT_TIMEOUT = 30L
        const val OK_HTTP_READ_TIMEOUT = 30L
        const val OK_HTTP_WRITE_TIMEOUT = 30L
    }

    //facebook
    @Autowired
    private lateinit var facebookApi: FacebookApi

    //google auth
    @Autowired
    private lateinit var googleIdTokenVerifier: GoogleIdTokenVerifier

    //facebook values
    @Value("\${my.api.admin.facebook.client_id}")
    private var facebookClientIdAdmin: Long? = null
    @Value("\${my.api.admin.facebook.client_secret}")
    private lateinit var facebookClientSecretAdmin: String

    @Value("\${my.api.game.facebook.client_id}")
    private var facebookClientIdGame: Long? = null
    @Value("\${my.api.game.facebook.client_secret}")
    private lateinit var facebookClientSecretGame: String

    private lateinit var facebookClientIds: Map<ScpQuizConstants.ClientApp, Long>
    private lateinit var facebookClientSecrets: Map<ScpQuizConstants.ClientApp, String>

    @PostConstruct
    fun initClassMembers() {
        facebookClientIds = mapOf(
                ScpQuizConstants.ClientApp.ADMIN to facebookClientIdAdmin!!,
                ScpQuizConstants.ClientApp.GAME to facebookClientIdGame!!
        )

        facebookClientSecrets = mapOf(
                ScpQuizConstants.ClientApp.ADMIN to facebookClientSecretAdmin,
                ScpQuizConstants.ClientApp.GAME to facebookClientSecretGame
        )
    }

    fun getUserDataFromProvider(
            provider: ScpQuizConstants.SocialProvider,
            token: String,
            clientApp: ScpQuizConstants.ClientApp
    ): CommonUserData = when (provider) {
        ScpQuizConstants.SocialProvider.GOOGLE -> {
            val googleIdToken: GoogleIdToken? = googleIdTokenVerifier.verify(token)
            googleIdToken?.let {
                val email = googleIdToken.payload.email ?: throw IllegalStateException("Can't login without email!")
                val avatar = googleIdToken.payload["picture"] as? String
                        ?: ScpQuizConstants.DEFAULT_AVATAR_URL
                val fullName = googleIdToken.payload["name"] as? String
                        ?: ScpQuizConstants.DEFAULT_FULL_NAME
                val firstName = googleIdToken.payload["given_name"] as? String
                        ?: ScpQuizConstants.DEFAULT_FULL_NAME
                val secondName = googleIdToken.payload["family_name"] as? String
                        ?: ScpQuizConstants.DEFAULT_FULL_NAME
                CommonUserData(
                        id = googleIdToken.payload.subject,
                        email = email,
                        firstName = firstName,
                        secondName = secondName,
                        fullName = fullName,
                        avatarUrl = avatar

                )
            } ?: throw IllegalStateException("Failed to verify idToken")
        }
        ScpQuizConstants.SocialProvider.FACEBOOK -> {
            val facebookClientId = facebookClientIds[clientApp]
            val facebookClientSecret = facebookClientSecrets[clientApp]
            val validatedTokenWrapper: ValidatedTokenWrapper = facebookApi
                    .debugToken(inputToken = token, accessToken = "$facebookClientId|$facebookClientSecret")
                    .map { ValidatedTokenWrapper(it, null) }
                    .onErrorReturn { ValidatedTokenWrapper(null, it) }
                    .blockingGet()

            validatedTokenWrapper.exception?.let {
                throw Exception(
                        if (it is HttpException) {
                            it.response()?.errorBody()?.string()
                        } else {
                            it.message
                        } ?: "unexpected error",
                        it
                )
            }

            if (validatedTokenWrapper.verifiedToken?.data?.appId != facebookClientId) {
                throw IllegalArgumentException("Facebook appId not equals correct one!")
            }

            val facebookProfile: FacebookProfileResponse = facebookApi.profile(token).blockingGet()
            val email = facebookProfile.email ?: throw IllegalStateException("Can't login without email!")
            CommonUserData(
                    id = facebookProfile.id.toString(),
                    email = email,
                    fullName = "${facebookProfile.firstName} ${facebookProfile.lastName}",
                    firstName = facebookProfile.firstName,
                    lastName = facebookProfile.lastName,
                    avatarUrl = facebookProfile.picture?.data?.url
            )
        }
        ScpQuizConstants.SocialProvider.VK -> {
            val commonUserData = ObjectMapper().readValue(token, CommonUserData::class.java)
            if (commonUserData.email == null) {
                throw IllegalStateException("Can't login without email!")
            }

            commonUserData
        }
    }
}