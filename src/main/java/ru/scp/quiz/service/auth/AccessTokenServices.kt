package ru.scp.quiz.service.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.stereotype.Service

@Service
class AccessTokenServices : ResourceServerTokenServices {

    @Autowired
    lateinit var tokenStore: TokenStore

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var clientService: ClientService

    override fun loadAuthentication(accessToken: String): OAuth2Authentication = tokenStore.readAuthentication(accessToken)

    override fun readAccessToken(accessToken: String): OAuth2AccessToken = tokenStore.readAccessToken(accessToken)

    /**
     * @param username aka email
     */
    fun deleteAllTokensByUserId(userId: Long) {
        val user = userService.getById(userId)
        val clients = clientService.findAll()
        clients.forEach {
            val accessTokens = tokenStore.findTokensByClientIdAndUserName(
                    it.client_id,
                    user.myUsername
            )
            accessTokens.forEach { accessToken ->
                val refreshToken = accessToken.refreshToken
                tokenStore.removeRefreshToken(refreshToken)
                tokenStore.removeAccessToken(accessToken)
            }
        }
    }
}