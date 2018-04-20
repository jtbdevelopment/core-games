package com.jtbdevelopment.games.security.spring.security;

import com.jtbdevelopment.games.security.spring.redirects.MobileAppChecker;
import com.jtbdevelopment.games.security.spring.redirects.MobileAppProperties;
import com.jtbdevelopment.games.security.spring.redirects.MobileAwareFailureAuthenticationHandler;
import com.jtbdevelopment.games.security.spring.redirects.MobileAwareSocialFailureAuthenticationHandler;
import com.jtbdevelopment.games.security.spring.redirects.MobileAwareSuccessfulAuthenticationHandler;
import com.jtbdevelopment.games.security.spring.security.cachecontrol.SmarterCacheControlHeaderWriter;
import com.jtbdevelopment.games.security.spring.security.cors.CorsFilter;
import com.jtbdevelopment.games.security.spring.security.cors.CorsHeaderWriter;
import com.jtbdevelopment.games.security.spring.security.csp.ContentSecurityPolicyHeaderWriter;
import com.jtbdevelopment.games.security.spring.security.csrf.XSRFTokenCookieFilter;
import com.jtbdevelopment.games.security.spring.security.facebook.FacebookCanvasAllowingProtectionMatcher;
import com.jtbdevelopment.games.security.spring.security.loginConfigurer.MobileAwareFormLoginConfigurer;
import com.jtbdevelopment.games.security.spring.social.MobileAwareSocialConfigurer;
import com.jtbdevelopment.games.security.spring.social.security.PlayerSocialUserDetailsService;
import com.jtbdevelopment.games.security.spring.userdetails.PlayerUserDetailsService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * Date: 1/12/15 Time: 6:51 PM
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
  private static final String LOGIN_PAGE = "/#/signin";
  private static final String LOGGED_IN_URL = "/#/signedin";
  private static final String LOGOUT_PAGE = "/signout";
  private static final String AUTHENTICATE_PAGE = "/signin/authenticate";
  @Autowired
  protected PlayerUserDetailsService playerUserDetailsService;
  @Autowired
  protected PlayerSocialUserDetailsService playerSocialUserDetailsService;
  @Autowired
  protected PasswordEncoder injectedPasswordEncoder;
  @Autowired
  protected SecurityProperties securityProperties;
  @Autowired
  protected PersistentTokenRepository persistentTokenRepository;
  @Autowired
  protected AuthenticationManagerBuilder authenticationManagerBuilder;
  @Autowired
  protected MobileAppProperties mobileAppProperties;
  @Autowired
  protected MobileAppChecker mobileAppChecker;

  protected static CsrfTokenRepository csrfTokenRepository() {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");
    return repository;
  }

  @PostConstruct
  public void configureAuthenticationProvider() throws Exception {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setPasswordEncoder(injectedPasswordEncoder);
    daoAuthenticationProvider.setUserDetailsService(playerUserDetailsService);
    authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider);
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    AuthenticationSuccessHandler successfulAuthenticationHandler = new MobileAwareSuccessfulAuthenticationHandler(
        mobileAppChecker, mobileAppProperties, LOGGED_IN_URL, true);

    MobileAwareFormLoginConfigurer mobileAwareFormLoginConfigurer = new MobileAwareFormLoginConfigurer(
        mobileAppChecker);
    mobileAwareFormLoginConfigurer.setBuilder(http);

    http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/api/**", "/livefeed/**").permitAll()
        .antMatchers("/favicon.ico", "/index.html", "/facebook/**", "/auth/**", "/signin/**",
            "/api/social/apis", "/images/**", "/views/**", "/templates/**", "/styles/**",
            "/scripts/**", "/bower_components/**", "/assets/**", "/*.js", "/*.css", "/*.svg",
            "/*.eot", "/*.ttf", "/*.woff*").permitAll().antMatchers("/**").authenticated().and()
        .apply(mobileAwareFormLoginConfigurer.successHandler(successfulAuthenticationHandler)
            .failureHandler(new MobileAwareFailureAuthenticationHandler(mobileAppChecker))
            .loginPage(LOGIN_PAGE).loginProcessingUrl(AUTHENTICATE_PAGE)).and().logout()
        .logoutUrl(LOGOUT_PAGE).deleteCookies("JSESSIONID").and().rememberMe()
        .tokenRepository(persistentTokenRepository).userDetailsService(playerUserDetailsService)
        .and().headers().frameOptions().disable()
        .addHeaderWriter(new ContentSecurityPolicyHeaderWriter()).and().headers().cacheControl()
        .disable().addHeaderWriter(new SmarterCacheControlHeaderWriter()).and().apply(
        new MobileAwareSocialConfigurer().successHandler(successfulAuthenticationHandler)
            .failureHandler(new MobileAwareSocialFailureAuthenticationHandler(mobileAppChecker,
                mobileAppProperties)));

    if (Boolean.parseBoolean(securityProperties.getAllowBasicAuth())) {
      logger.warn("-----------------------------------------------------");
      logger.warn("-----------------------------------------------------");
      logger.warn("-----------------------------------------------------");
      logger.warn("Allowing Basic Auth!  Should only be in test systems!");
      logger.warn("Disabling https requirements as well!                ");
      logger.warn("-----------------------------------------------------");
      logger.warn("-----------------------------------------------------");
      logger.warn("-----------------------------------------------------");
      http.httpBasic().and().csrf().disable().headers().addHeaderWriter(new CorsHeaderWriter());
      http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);
    } else {
      http.rememberMe().useSecureCookie(true);
      http.csrf().csrfTokenRepository(csrfTokenRepository())
          .requireCsrfProtectionMatcher(new FacebookCanvasAllowingProtectionMatcher()).and()
          .addFilterAfter(new XSRFTokenCookieFilter(), CsrfFilter.class);
    }

  }
}
