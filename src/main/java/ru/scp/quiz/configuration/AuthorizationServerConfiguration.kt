package ru.scp.quiz.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import ru.scp.quiz.service.auth.ClientServiceImpl
import ru.scp.quiz.service.auth.UserServiceImpl
import javax.sql.DataSource


@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfiguration : AuthorizationServerConfigurerAdapter() {

    @Autowired
    private lateinit var dataSource: DataSource

    @Bean
    fun tokenStore(): TokenStore = JdbcTokenStore(dataSource)

    @Autowired
    private lateinit var clientDetailsService: ClientServiceImpl

    @Autowired
    private lateinit var userDetailsService: UserServiceImpl

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients
                .withClientDetails(clientDetailsService)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
    }
}