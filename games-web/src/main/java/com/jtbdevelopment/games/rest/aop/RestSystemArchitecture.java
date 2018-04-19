package com.jtbdevelopment.games.rest.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Date: 11/12/14 Time: 7:06 PM
 */
@Aspect
@Component
public class RestSystemArchitecture {

  @Pointcut("within(com.jtbdevelopment..*.rest.services..*)")
  public void inRestServices() {
  }

}
