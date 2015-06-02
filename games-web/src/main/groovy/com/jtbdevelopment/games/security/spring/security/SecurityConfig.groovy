package com.jtbdevelopment.games.security.spring.security

import com.jtbdevelopment.games.security.spring.security.csrf.XSRFTokenCookieFilter
import com.jtbdevelopment.games.security.spring.security.facebook.FacebookCanvasAllowingProtectionMatcher
import com.jtbdevelopment.games.security.spring.security.facebook.FacebookCanvasXFrameAllowFromStrategy
import com.jtbdevelopment.games.security.spring.social.security.PlayerSocialUserDetailsService
import com.jtbdevelopment.games.security.spring.userdetails.PlayerUserDetailsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.PortMapperImpl
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter
import org.springframework.social.security.SpringSocialConfigurer

import javax.annotation.PostConstruct

/**
 * Date: 1/12/15
 * Time: 6:51 PM
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public static final String NO_REDIRECT = 'noRedirect'

    @Autowired
    PlayerUserDetailsService playerUserDetailsService

    @Autowired
    PlayerSocialUserDetailsService playerSocialUserDetailsService

    @Autowired
    PasswordEncoder injectedPasswordEncoder

    @Autowired
    SecurityProperties securityProperties

    @Autowired
    PersistentTokenRepository persistentTokenRepository

    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder

    @PostConstruct
    public void configureAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider()
        daoAuthenticationProvider.setPasswordEncoder(injectedPasswordEncoder)
        daoAuthenticationProvider.setUserDetailsService(playerUserDetailsService)
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider)
    }

    protected CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository()
        repository.setHeaderName('X-XSRF-TOKEN')
        repository
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        PortMapperImpl portMapper = new PortMapperImpl()
        portMapper.getTranslatedPortMappings().put(8998, 8999)
        portMapper.getTranslatedPortMappings().put(8998, 8999)
        portMapper.getTranslatedPortMappings().put(8090, 8943)
        http.authorizeRequests().
                antMatchers(
                        "/favicon.ico",
                        "/images/**",
                        "/styles/**",
                        "/scripts/**",
                        "/facebook/**",
                        "/auth/**",
                        "/signin/**",
                        "/api/social/apis"
                ).
                permitAll().
                antMatchers("/**").authenticated().
                and().formLogin().loginPage("/signin/index.html").loginProcessingUrl("/signin/authenticate").failureUrl("/signin/index.html?error=BadCredentials").defaultSuccessUrl("/", true).successHandler(new ConfigurableSuccessfulAuthenticationHandler()).failureHandler(new ConfigurableFailureAuthenticationHandler()).
                and().logout().logoutUrl("/signout").deleteCookies("JSESSIONID").
                and().rememberMe().tokenRepository(persistentTokenRepository).userDetailsService(playerUserDetailsService).
                and().portMapper().portMapper(portMapper).
                and().headers().addHeaderWriter(new XFrameOptionsHeaderWriter(new FacebookCanvasXFrameAllowFromStrategy())).
                and().apply(new SpringSocialConfigurer().postLoginUrl("/"))

        if (Boolean.parseBoolean(securityProperties.getAllowBasicAuth())) {
            logger.warn("-----------------------------------------------------")
            logger.warn("-----------------------------------------------------")
            logger.warn("-----------------------------------------------------")
            logger.warn("Allowing Basic Auth!  Should only be in test systems!")
            logger.warn("Disabling https requirements as well!                ")
            logger.warn("-----------------------------------------------------")
            logger.warn("-----------------------------------------------------")
            logger.warn("-----------------------------------------------------")
            http.httpBasic().and().csrf().disable()
        } else {
            http.requiresChannel().antMatchers("/**").requiresSecure()
            http.rememberMe().useSecureCookie(true)
            http.csrf().csrfTokenRepository(csrfTokenRepository()).requireCsrfProtectionMatcher(new FacebookCanvasAllowingProtectionMatcher()).
                    and().addFilterAfter(new XSRFTokenCookieFilter(), CsrfFilter.class)
        }
    }
}
