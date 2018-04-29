package com.jtbdevelopment.games.rest.aop;

import java.lang.reflect.Method;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 12/22/2014 Time: 10:30 PM
 */
public class RestSystemArchitectureTest {

  @Test
  public void testClassAnnotationIsPresent() {
    Assert.assertTrue(RestSystemArchitecture.class.isAnnotationPresent(Aspect.class));
  }

  @Test
  public void testRestServicesPointCutAnnotation() throws NoSuchMethodException {
    Method m = RestSystemArchitecture.class.getMethod("inRestServices", new Class<?>[0]);
    Pointcut p = m.getAnnotation(Pointcut.class);
    Assert.assertNotNull(p);
    Assert.assertEquals("within(com.jtbdevelopment..*.rest.services..*)", p.value());
  }

}
