package com.jtbdevelopment.games.push;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 10/16/15 Time: 6:59 PM
 */
public class PushPropertiesTest {

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
