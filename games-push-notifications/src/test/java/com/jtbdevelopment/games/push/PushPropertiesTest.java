package com.jtbdevelopment.games.push;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

/**
 * Date: 10/16/15 Time: 6:59 PM
 */
public class PushPropertiesTest {

  @Test
  public void testConstructorParamAnnotations() throws NoSuchMethodException {
    Constructor c = PushProperties.class
        .getConstructor(String.class, String.class);
    Annotation[][] annotations = c.getParameterAnnotations();
    assertEquals(2, annotations.length);
    assertEquals(Value.class, annotations[0][0].annotationType());
    assertEquals(Value.class, annotations[1][0].annotationType());
    assertEquals("${push.senderID:}", ((Value) annotations[0][0]).value());
    assertEquals("${push.apiKey:}", ((Value) annotations[1][0]).value());
  }

  @Test
  public void testEnabledFlagSetWithBothValuesSet() {
    PushProperties properties = new PushProperties("1234", "3x4");
    Assert.assertTrue(properties.isEnabled());
    Assert.assertEquals("1234", properties.getSenderID());
    Assert.assertEquals("3x4", properties.getApiKey());
  }

  @Test
  public void testEnabledFlagSetWithNoAPIKey() {
    PushProperties properties = new PushProperties("1234", "");
    Assert.assertFalse(properties.isEnabled());
  }

  @Test
  public void testEnabledFlagSetWithNoSenderID() {
    PushProperties properties = new PushProperties(null, "124z");
    Assert.assertFalse(properties.isEnabled());
  }

}
