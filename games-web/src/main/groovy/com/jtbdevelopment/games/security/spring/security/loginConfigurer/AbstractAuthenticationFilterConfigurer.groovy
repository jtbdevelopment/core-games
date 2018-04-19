package com.jtbdevelopment.games.security.spring.security.loginConfigurer

import com.jtbdevelopment.games.security.spring.redirects.MobileAppChecker
import com.jtbdevelopment.games.security.spring.redirects.MobileAwareLoginUrlAuthenticationEntryPoint
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.web.PortMapper
import org.springframework.security.web.authentication.*
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.accept.ContentNegotiationStrategy
import org.springframework.web.accept.HeaderContentNegotiationStrategy

import javax.servlet.http.HttpServletRequest

/**
 * Date: 8/29/2015
 * Time: 2:49 PM
 *
 * Largely cut-n-paste of spring security class of same name except permitAll references completely removed and other JTB comments below
 */
abstract class AbstractAuthenticationFilterConfigurer
//  JTB simplified template params for groovy
        extends AbstractHttpConfigurer {

    private final UsernamePasswordAuthenticationFilter authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();

    private MobileAwareLoginUrlAuthenticationEntryPoint authenticationEntryPoint;

    private boolean customLoginPage;
    private String loginPage;
    private String loginProcessingUrl;

    private AuthenticationFailureHandler failureHandler;

    private String failureUrl;

    private final MobileAppChecker mobileAppChecker

    protected AbstractAuthenticationFilterConfigurer(UsernamePasswordAuthenticationFilter authenticationFilter,
                                                     String defaultLoginProcessingUrl,
                                                     MobileAppChecker mobileAppChecker) {
        this.authFilter = authenticationFilter;
        this.mobileAppChecker = mobileAppChecker
        setLoginPage("/login");
        if (defaultLoginProcessingUrl != null) {
            loginProcessingUrl(defaultLoginProcessingUrl);
        }
    }

    //  JTB - groovy collapsing of overload
    @SuppressWarnings("GroovyUnusedDeclaration")
    public
    final MobileAwareFormLoginConfigurer defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse = false) {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(defaultSuccessUrl);
        handler.setAlwaysUseDefaultTargetUrl(alwaysUse);
        return successHandler(handler);
    }

    public MobileAwareFormLoginConfigurer loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        authFilter
                .setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
        return getSelf();
    }

    protected abstract RequestMatcher createLoginProcessingUrlMatcher(
            String loginProcessingUrl);

    @SuppressWarnings("GroovyUnusedDeclaration")
    public final MobileAwareFormLoginConfigurer authenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return getSelf();
    }

    public
    final MobileAwareFormLoginConfigurer successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return getSelf();
    }

    public final MobileAwareFormLoginConfigurer failureUrl(String authenticationFailureUrl) {
        MobileAwareFormLoginConfigurer result = failureHandler(new SimpleUrlAuthenticationFailureHandler(
                authenticationFailureUrl));
        this.failureUrl = authenticationFailureUrl;
        return result;
    }

    public final MobileAwareFormLoginConfigurer failureHandler(
            AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureUrl = null;
        this.failureHandler = authenticationFailureHandler;
        return getSelf();
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        updateAuthenticationDefaults();

        registerDefaultAuthenticationEntryPoint(http);
    }

    @SuppressWarnings("unchecked")
    private void registerDefaultAuthenticationEntryPoint(HttpSecurity http) {
        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = http
                .getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling == null) {
            return;
        }
        ContentNegotiationStrategy contentNegotiationStrategy = http
                .getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }
        MediaTypeRequestMatcher preferredMatcher = new MediaTypeRequestMatcher(
                contentNegotiationStrategy, MediaType.APPLICATION_XHTML_XML,
                new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
        preferredMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        exceptionHandling.defaultAuthenticationEntryPointFor(
                postProcess(authenticationEntryPoint), preferredMatcher);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        PortMapper portMapper = http.getSharedObject(PortMapper.class);
        if (portMapper != null) {
            authenticationEntryPoint.setPortMapper(portMapper);
        }

        authFilter.setAuthenticationManager(http
                .getSharedObject(AuthenticationManager.class));
        authFilter.setAuthenticationSuccessHandler(successHandler);
        authFilter.setAuthenticationFailureHandler(failureHandler);
        if (authenticationDetailsSource != null) {
            authFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
        }
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http
                .getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            authFilter.setRememberMeServices(rememberMeServices);
        }
        UsernamePasswordAuthenticationFilter filter = postProcess(authFilter);
        http.addFilter(filter);
    }

    protected MobileAwareFormLoginConfigurer loginPage(String loginPage) {
        setLoginPage(loginPage);
        updateAuthenticationDefaults();
        this.customLoginPage = true;
        return getSelf();
    }

    public final boolean isCustomLoginPage() {
        return customLoginPage;
    }

    protected final UsernamePasswordAuthenticationFilter getAuthenticationFilter() {
        return authFilter;
    }

    protected final String getLoginPage() {
        return loginPage;
    }

    protected final String getLoginProcessingUrl() {
        return loginProcessingUrl;
    }

    protected final String getFailureUrl() {
        return failureUrl;
    }

    private void updateAuthenticationDefaults() {
        if (loginProcessingUrl == null) {
            loginProcessingUrl(loginPage);
        }
        if (failureHandler == null) {
            failureUrl(loginPage + "?error");
        }

        final LogoutConfigurer<HttpSecurity> logoutConfigurer = getBuilder().getConfigurer(
                LogoutConfigurer.class);
        //  JTB - Commented out if since no access and false false in our case
//        if (logoutConfigurer != null && !logoutConfigurer.isCustomLogoutSuccess()) {
        logoutConfigurer.logoutSuccessUrl(loginPage + "?logout");
//        }
    }

    private void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
        //  JTB - customized
        this.authenticationEntryPoint = new MobileAwareLoginUrlAuthenticationEntryPoint(loginPage, mobileAppChecker);
    }

    @SuppressWarnings("unchecked")
    private MobileAwareFormLoginConfigurer getSelf() {
        return (MobileAwareFormLoginConfigurer) this;
    }

}
