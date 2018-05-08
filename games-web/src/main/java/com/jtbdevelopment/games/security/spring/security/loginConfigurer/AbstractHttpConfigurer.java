package com.jtbdevelopment.games.security.spring.security.loginConfigurer;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * Date: 8/29/2015 Time: 2:56 PM
 *
 * Largely cut-n-paste of spring security class to enable override
 */
@SuppressWarnings("unused")
public abstract class AbstractHttpConfigurer extends
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  /**
   * Disables the {@link AbstractHttpConfigurer} by removing it. After doing so a fresh version of
   * the configuration can be applied.
   *
   * @return the {@link org.springframework.security.config.annotation.web.HttpSecurityBuilder} for
   * additional customizations
   */
  public HttpSecurity disable() {
    //  JTB - minor groovy change here
    //noinspection unchecked
    getBuilder().removeConfigurer((Class<AbstractHttpConfigurer>) this.getClass());
    return getBuilder();
  }

  public MobileAwareFormLoginConfigurer withObjectPostProcessor(
      ObjectPostProcessor<?> objectPostProcessor) {
    addObjectPostProcessor(objectPostProcessor);
    return (MobileAwareFormLoginConfigurer) this;
  }

}
