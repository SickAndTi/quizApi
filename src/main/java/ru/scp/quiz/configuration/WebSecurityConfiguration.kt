package ru.scp.quiz.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import ru.scp.quiz.ScpQuizConstants
import ru.scp.quiz.service.auth.ClientServiceImpl
import ru.scp.quiz.service.auth.UserServiceImpl
import javax.servlet.Filter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class WebSecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var clientDetailsService: ClientServiceImpl

    @Autowired
    private lateinit var tokenStore: TokenStore

    @Bean
    fun tokenServices() = DefaultTokenServices().apply {
        setTokenStore(tokenStore)
        setClientDetailsService(clientDetailsService)
        setAuthenticationManager(authenticationManager())
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider =
            DaoAuthenticationProvider().apply {
                setUserDetailsService(userDetailsService)
                setPasswordEncoder(passwordEncoder())
            }

    @Primary
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager =
            super.authenticationManagerBean()

    @Autowired
    lateinit var userDetailsService: UserServiceImpl

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                .authenticationProvider(authenticationProvider())
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
    }

    @Bean
    fun oauth2authenticationManager(): OAuth2AuthenticationManager =
            OAuth2AuthenticationManager().apply {
                setClientDetailsService(clientDetailsService)
                setTokenServices(tokenServices())
            }

    @Bean
    fun myOAuth2Filter(): Filter =
            OAuth2AuthenticationProcessingFilter().apply {
                setAuthenticationManager(oauth2authenticationManager())
                //allow auth with cookies (not only with token)
                setStateless(false)
            }

    @Value("\${angular.port}")
    lateinit var angularServerPort: String

    @Value("\${angular.href}")
    lateinit var angularServerHref: String

    @Value("\${my.ssl.pin}")
    lateinit var sslPin: String

    override fun configure(http: HttpSecurity) {
        http.headers().httpPublicKeyPinning()
                .addSha256Pins(sslPin)
                .reportOnly(false)
                .includeSubDomains(true)

        http
                .cors()
        http
                .csrf()
                .disable()
        http
                .authorizeRequests()
                .antMatchers("/", "/login**", "/error**","/leaderboard/getLeaderboard")
                .permitAll()
        http
                .authorizeRequests()
                .antMatchers("/quiz/all")
                .hasAnyAuthority("ADMIN", "USER", "APP")
                .anyRequest()
                .hasAnyAuthority("ADMIN", "USER")
        http
                .formLogin()
                .successHandler { request, response, _ ->
                    println("angular.port: $angularServerPort")
                    println("request: ${request.localName}/${request.localAddr}/${request.localPort}/${request.serverName}")
                    DefaultRedirectStrategy().sendRedirect(
                            request,
                            response,
                            "${request.scheme}://${request.serverName}$angularServerPort$angularServerHref"
                    );
                }
                .and()
                .logout()
                .logoutSuccessHandler { request, response, _ ->
                    println("angular.port: $angularServerPort")
                    println("request: ${request.localName}/${request.localAddr}/${request.localPort}/${request.serverName}")
                    DefaultRedirectStrategy().sendRedirect(
                            request,
                            response,
                            "${request.scheme}://${request.serverName}$angularServerPort$angularServerHref"
                    );
                }
                .permitAll()
        http
                .addFilterBefore(
                        myOAuth2Filter(),
                        BasicAuthenticationFilter::class.java
                )
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
                "/${ScpQuizConstants.Path.AUTH}/**"
        )
    }
}