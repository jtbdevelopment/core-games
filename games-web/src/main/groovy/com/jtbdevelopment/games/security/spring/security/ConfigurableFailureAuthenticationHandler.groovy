package com.jtbdevelopment.games.security.spring.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Date: 6/2/15
 * Time: 3:32 PM
 */
class ConfigurableFailureAuthenticationHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    void onAuthenticationFailure(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception) throws IOException, ServletException {
        if (request.parameterMap.containsKey(SecurityConfig.NO_REDIRECT)) {
            String[] parameterValues = request.parameterMap[SecurityConfig.NO_REDIRECT]
            if (parameterValues != null && parameterValues.length > 0 && "true" == parameterValues[0]) {
                logger.debug("No redirect set to true, sending 401 Unauthorized error");

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
            }
        }

        super.onAuthenticationFailure(request, response, exception)
    }
}
