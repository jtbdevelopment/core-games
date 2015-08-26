package com.jtbdevelopment.games.security.spring.redirects

import org.springframework.mock.web.MockHttpServletRequest

/**
 * Date: 6/13/15
 * Time: 7:41 PM
 */
class MobileAppCheckerTest extends GroovyTestCase {
    MobileAppChecker checker = new MobileAppChecker()

    void testReturnsTrueIfHeaderIsFoundAndStartsWithFile() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.addHeader("Origin", "file://something")
        assert checker.isMobileRequest(request)
    }

    void testReturnsFalseIfHeaderIsFoundAndDoesNotStartWithFile() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.addHeader("Origin", "http://file://something")
        assertFalse checker.isMobileRequest(request)
    }

    void testReturnsFalseIfNoHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        assertFalse checker.isMobileRequest(request)
    }

    void testReturnsFalseIfNoHeaderOfName() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.addHeader('SomeHeader', 'SomeValue');
        assertFalse checker.isMobileRequest(request)
    }

    void testReturnsFalseIfNoRequest() {
        assertFalse checker.isMobileRequest(null)
    }
}
