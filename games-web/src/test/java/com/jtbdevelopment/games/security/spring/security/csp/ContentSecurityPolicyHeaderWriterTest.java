package com.jtbdevelopment.games.security.spring.security.csp;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Date: 7/31/15 Time: 10:30 PM
 */
public class ContentSecurityPolicyHeaderWriterTest {

  private static final String CSP = "Content-Security-Policy";
  private ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter();

  @Test
  public void testWriteHeaders() {
    MockHttpServletResponse response = new MockHttpServletResponse();

    writer.writeHeaders(null, response);

    Assert.assertTrue(response.containsHeader(CSP));
    Assert.assertEquals("frame-ancestors 'self' *.facebook.com ;", response.getHeader(CSP));
  }
}
