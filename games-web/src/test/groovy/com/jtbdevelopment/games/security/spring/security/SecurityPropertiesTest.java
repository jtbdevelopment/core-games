package com.jtbdevelopment.games.security.spring.security;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

/**
 * Date: 1/12/15 Time: 6:43 PM
 */
public class SecurityPropertiesTest {

  @Test
  public void testConstructorParamAnnotations() throws NoSuchMethodException {
    Constructor c = SecurityProperties.class.getConstructor(String.class);
    Annotation[][] annotations = c.getParameterAnnotations();
    assertEquals(1, annotations.length);
    assertEquals(Value.class, annotations[0][0].annotationType());
    assertEquals("${http.allowBasicAuth:false}", ((Value) annotations[0][0]).value());
  }

}
