package com.jtbdevelopment.games.security.spring.security.csrf;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * Date: 1/23/15 Time: 5:27 PM
 *
 * Based on https://spring.io/blog/2015/01/12/the-login-page-angular-js-and-spring-security-part-ii
 */
public class XSRFTokenCookieFilter extends OncePerRequestFilter {

    private static final String XSRF_TOKEN = "XSRF-TOKEN";

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
        final HttpServletResponse response, final FilterChain filterChain)
        throws ServletException, IOException {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            Cookie cookie = WebUtils.getCookie(request, XSRF_TOKEN);
            String token = csrf.getToken();
            if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                cookie = new Cookie(XSRF_TOKEN, token);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            //  Useful for mobile
            response.addHeader(XSRF_TOKEN, cookie.getValue());
        }

        filterChain.doFilter(request, response);
    }
}
