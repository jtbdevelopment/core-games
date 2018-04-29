package com.jtbdevelopment.games.security.spring.redirects;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

/**
 * Date: 6/14/15 Time: 7:28 AM
 */
public class MobileAppPropertiesTest {

  @Test
  public void testConstructorParamAnnotations() throws NoSuchMethodException {
    Constructor c = MobileAppProperties.class.getConstructor(String.class, String.class);
    Annotation[][] annotations = c.getParameterAnnotations();
    assertEquals(2, annotations.length);
    assertEquals(Value.class, annotations[0][0].annotationType());
    assertEquals(Value.class, annotations[1][0].annotationType());
    assertEquals("${mobile.success.url:/api/security}", ((Value) annotations[0][0]).value());
    assertEquals("${mobile.failure.url:#/app/signin}", ((Value) annotations[1][0]).value());
  }

}
