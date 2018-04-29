package com.jtbdevelopment.games.rest.aop;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Date: 2/23/15 Time: 7:26 PM
 */
public class AspectConfigurationTest {

  @Test
  public void testAnnotations() {
    Assert.assertTrue(AspectConfiguration.class.isAnnotationPresent(EnableAspectJAutoProxy.class));
    Assert.assertTrue(AspectConfiguration.class.isAnnotationPresent(Configuration.class));
  }

}
