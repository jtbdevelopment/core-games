package com.jtbdevelopment.games.security.spring.redirects

import org.springframework.mock.web.MockHttpServletRequest

import javax.servlet.http.Cookie

/**
 * Date: 6/13/15
 * Time: 7:41 PM
 */
class MobileAppCheckerTest extends GroovyTestCase {
    private String MOBILE_COOKIE_NAME = 'IMobile'
    MobileAppChecker checker = new MobileAppChecker(mobileCookie: MOBILE_COOKIE_NAME)

    void testReturnsTrueIfCookieIsFoundAndSetToTrue() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.cookies = [new Cookie(MOBILE_COOKIE_NAME, 'true')]
        assert checker.isMobileRequest(request)
    }

    void testReturnsTrueIfCookieIsFoundAndSetToFalse() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.cookies = [new Cookie(MOBILE_COOKIE_NAME, 'false')]
        assertFalse checker.isMobileRequest(request)
    }

    void testReturnsFalseIfNoCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        assertFalse checker.isMobileRequest(request)
    }

    void testReturnsFalseIfNoCookieOfName() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.cookies = [new Cookie('OTHER_COOKIE', 'true')]
        assertFalse checker.isMobileRequest(request)
    }

    void testReturnsFalseIfNoRequest() {
        assertFalse checker.isMobileRequest(null)
    }
}
