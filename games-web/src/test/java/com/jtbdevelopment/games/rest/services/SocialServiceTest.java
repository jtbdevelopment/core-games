package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.security.spring.social.facebook.FacebookProperties;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/8/15 Time: 9:49 PM
 */
public class SocialServiceTest {

  private FacebookProperties facebookProperties = new FacebookProperties("ID", "PASS", "X, Y");
  private SocialService service = new SocialService(facebookProperties);

  @Test
  public void testClassAnnotations() {
    Assert.assertTrue(SocialService.class.isAnnotationPresent(Path.class));
    Assert.assertEquals("social", SocialService.class.getAnnotation(Path.class).value());
  }

  @Test
  public void testApiInfoAnnotations() throws NoSuchMethodException {
    Method m = SocialService.class.getMethod("apiInfo");
    Assert.assertNotNull(m);
    Assert.assertTrue(m.isAnnotationPresent(GET.class));
    Assert.assertArrayEquals(
        new ArrayList<String>(Arrays.asList(MediaType.APPLICATION_JSON)).toArray(),
        m.getAnnotation(Produces.class).value());
    Assert.assertEquals("apis", m.getAnnotation(Path.class).value());
  }

  @Test
  public void testRestWithNullProperties() {
    service = new SocialService(null);
    Assert.assertEquals(new LinkedHashMap(), service.apiInfo());
  }

  @Test
  public void testRestWithWarningProperties() {
    facebookProperties = new FacebookProperties("", null, null);
    service = new SocialService(facebookProperties);
    Assert.assertEquals(new LinkedHashMap(), service.apiInfo());
  }

  @Test
  public void testRestWithGoodProperties() {
    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(2);
    map.put("facebookAppId", "ID");
    map.put("facebookPermissions", "X, Y");
    Assert.assertEquals(map, service.apiInfo());
  }
}
