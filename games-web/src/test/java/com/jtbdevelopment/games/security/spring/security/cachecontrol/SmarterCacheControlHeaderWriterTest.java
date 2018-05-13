package com.jtbdevelopment.games.security.spring.security.cachecontrol;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Date: 6/8/15 Time: 7:03 AM
 */
public class SmarterCacheControlHeaderWriterTest {

  private static final String EXPIRES = "Expires";
  private static final String PRAGMA = "Pragma";
  private static final String CACHE_CONTROL = "Cache-Control";
  private SmarterCacheControlHeaderWriter writer = new SmarterCacheControlHeaderWriter();

  @Test
  public void testWriteHeadersWithCache() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setServletPath("/images/animage.png");

    Instant nowPlus1Hour = Instant.now().plusSeconds(60 * 60);
    Instant plus30seconds = nowPlus1Hour.plusSeconds(30);

    writer.writeHeaders(request, response);

    assertTrue(response.containsHeader(EXPIRES));
    assertTrue(Long.parseLong(response.getHeader(EXPIRES)) >= nowPlus1Hour.getEpochSecond());
    assertTrue(Long.parseLong(response.getHeader(EXPIRES)) <= plus30seconds.getEpochSecond());
    assertFalse(response.containsHeader(PRAGMA));
    assertFalse(response.containsHeader(CACHE_CONTROL));
  }

  @Test
  public void testWriteHeadersAllowsCachingFor() {

    Arrays.asList("/images", "/scripts", "/styles", "/bower_components", "/views")
        .forEach(baseUrl -> {
          MockHttpServletRequest request = new MockHttpServletRequest();
          MockHttpServletResponse response = new MockHttpServletResponse();

          request.setServletPath(baseUrl + "X");

          writer.writeHeaders(request, response);

          assertTrue(response.containsHeader(EXPIRES));
          Assert.assertNotEquals("0", response.getHeader(EXPIRES));
          assertFalse(response.containsHeader(PRAGMA));
          assertFalse(response.containsHeader(CACHE_CONTROL));

        });
  }

  @Test
  public void testWriteHeadersWithNoCache() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setServletPath("/other/animage.png");

    writer.writeHeaders(request, response);

    assertTrue(response.containsHeader(EXPIRES));
    assertTrue(response.containsHeader(PRAGMA));
    assertTrue(response.containsHeader(CACHE_CONTROL));
    Assert.assertEquals("0", response.getHeader(EXPIRES));
    Assert.assertEquals("no-cache", response.getHeader(PRAGMA));
    Assert.assertEquals("no-cache, no-store, max-age=0, must-revalidate",
        response.getHeader(CACHE_CONTROL));
  }
}
