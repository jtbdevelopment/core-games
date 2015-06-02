package com.jtbdevelopment.games.security.spring.security

import groovy.transform.CompileStatic
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Date: 6/2/15
 * Time: 6:44 AM
 */
@CompileStatic
class ConfigurableSuccessfulAuthenticationHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication) throws ServletException, IOException {
        if (request.parameterMap.containsKey(SecurityConfig.NO_REDIRECT)) {
            String[] parameterValues = request.parameterMap[SecurityConfig.NO_REDIRECT]
            if (parameterValues != null && parameterValues.length > 0 && "true" == parameterValues[0]) {
                return
            }
        }
        super.onAuthenticationSuccess(request, response, authentication)
    }
}
