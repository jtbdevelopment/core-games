package com.jtbdevelopment.games.security.spring.security.loginConfigurer;

import com.jtbdevelopment.games.security.spring.redirects.MobileAppChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Date: 8/29/2015 Time: 2:44 PM
 *
 * Cut-n-paste of spring security class of same name to provide for overriding behaviour in
 * AbstractAuthenticationFilterConfigurer
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class MobileAwareFormLoginConfigurer extends AbstractAuthenticationFilterConfigurer {

  public MobileAwareFormLoginConfigurer(MobileAppChecker mobileAppChecker) {
    super(new UsernamePasswordAuthenticationFilter(), null, mobileAppChecker);
    usernameParameter("username");
    passwordParameter("password");
  }

  public MobileAwareFormLoginConfigurer loginPage(String loginPage) {
    return super.loginPage(loginPage);
  }

  public MobileAwareFormLoginConfigurer usernameParameter(String usernameParameter) {
    getAuthenticationFilter().setUsernameParameter(usernameParameter);
    return this;
  }

  public MobileAwareFormLoginConfigurer passwordParameter(String passwordParameter) {
    getAuthenticationFilter().setPasswordParameter(passwordParameter);
    return this;
  }

  @Override
  public void init(HttpSecurity http) throws Exception {
    super.init(http);
    initDefaultLoginFilter(http);
  }

  @Override
  protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
    return new AntPathRequestMatcher(loginProcessingUrl, "POST");
  }

  private String getUsernameParameter() {
    return getAuthenticationFilter().getUsernameParameter();
  }

  private String getPasswordParameter() {
    return getAuthenticationFilter().getPasswordParameter();
  }

  private void initDefaultLoginFilter(HttpSecurity http) {
    DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
        .getSharedObject(DefaultLoginPageGeneratingFilter.class);
    if (loginPageGeneratingFilter != null && !isCustomLoginPage()) {
      loginPageGeneratingFilter.setFormLoginEnabled(true);
      loginPageGeneratingFilter.setUsernameParameter(getUsernameParameter());
      loginPageGeneratingFilter.setPasswordParameter(getPasswordParameter());
      loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
      loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
      loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
    }

  }

}
