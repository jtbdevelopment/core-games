package com.jtbdevelopment.games.security.spring.redirects;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Date: 6/13/15 Time: 7:41 PM
 */
public class MobileAppCheckerTest {

  private MobileAppChecker checker = new MobileAppChecker();

  @Test
  public void testReturnsTrueIfHeaderIsFoundAndStartsWithFile() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Origin", "file://something");
    Assert.assertTrue(checker.isMobileRequest(request));
  }

  @Test
  public void testReturnsFalseIfHeaderIsFoundAndDoesNotStartWithFile() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Origin", "http://file://something");
    Assert.assertFalse(checker.isMobileRequest(request));
  }

  @Test
  public void testReturnsFalseIfNoHeader() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    Assert.assertFalse(checker.isMobileRequest(request));
  }

  @Test
  public void testReturnsFalseIfNoHeaderOfName() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("SomeHeader", "SomeValue");
    Assert.assertFalse(checker.isMobileRequest(request));
  }

  @Test
  public void testReturnsFalseIfNoRequest() {
    Assert.assertFalse(checker.isMobileRequest(null));
  }
}
