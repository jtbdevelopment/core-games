package com.jtbdevelopment.games.security.spring.social.facebook;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

/**
 * Date: 1/7/15 Time: 7:39 PM
 */
public class FacebookPropertiesTest {

  @Test
  public void testConstructorParamAnnotations() throws NoSuchMethodException {
    Constructor c = FacebookProperties.class
        .getConstructor(String.class, String.class, String.class);
    Annotation[][] annotations = c.getParameterAnnotations();
    assertEquals(3, annotations.length);
    assertEquals(Value.class, annotations[0][0].annotationType());
    assertEquals(Value.class, annotations[1][0].annotationType());
    assertEquals(Value.class, annotations[2][0].annotationType());
    assertEquals("${facebook.clientID:}", ((Value) annotations[0][0]).value());
    assertEquals("${facebook.clientSecret:}", ((Value) annotations[1][0]).value());
    assertEquals("${facebook.permissions:public_profile,user_friends}",
        ((Value) annotations[2][0]).value());
  }

  @Test
  public void testGeneratesWarningOnBothNull() {
    FacebookProperties properties = new FacebookProperties(null, null, "x, y, z");
    Assert.assertTrue(properties.isWarnings());
  }

  @Test
  public void testGeneratesWarningOnBothBlank() {
    FacebookProperties properties = new FacebookProperties("", "", "x, y, z");
    Assert.assertTrue(properties.isWarnings());
  }

  @Test
  public void testGeneratesWarningOnSecret() {
    FacebookProperties properties = new FacebookProperties("", "SET", "x, y, z");
    Assert.assertTrue(properties.isWarnings());
  }

  @Test
  public void testGeneratesWarningOnAppID() {
    FacebookProperties properties = new FacebookProperties("SET", "", "x, y, z");
    Assert.assertTrue(properties.isWarnings());
  }

  @Test
  public void testNoWarningWhenPropertiesSet() {
    FacebookProperties properties = new FacebookProperties("SET", "SET", "x, y, z");
    Assert.assertFalse(properties.isWarnings());
  }

}
