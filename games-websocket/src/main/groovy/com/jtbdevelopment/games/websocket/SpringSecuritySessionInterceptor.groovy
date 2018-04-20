package com.jtbdevelopment.games.websocket

import com.jtbdevelopment.games.security.SessionUserInfo
import groovy.transform.CompileStatic
import org.atmosphere.cpr.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Date: 12/18/14
 * Time: 6:53 PM
 *
 * Requires
 * <init-param>
 *  <param-name>org.atmosphere.cpr.sessionSupport</param-name>
 *  <param-value>true</param-value>
 * </init-param>
 */
@CompileStatic
class SpringSecuritySessionInterceptor implements AtmosphereInterceptor {
    private static
    final Logger logger = LoggerFactory.getLogger(SpringSecuritySessionInterceptor.class)
    public static final String SPRING__SECURITY__CONTEXT = "SPRING_SECURITY_CONTEXT"

    @Override
    void configure(final AtmosphereConfig config) {

    }

    @Override
    Action inspect(final AtmosphereResource r) {
        try {
            retrieveSpringSecurityContext(r)

            AtmosphereRequest request = r.request
            String requestUserId = request.pathInfo
            String expectedRequestPath = "/" + getSessionUserId()
            if (requestUserId != expectedRequestPath) {
                logger.warn("INVALID REQUEST FOR " + requestUserId + " FROM " + getSessionUserId())
                return Action.CANCELLED
            }
            return Action.CONTINUE
        } catch (Exception e) {
            logger.warn("Exception determining Security of Atmosphere Session", e)
            return Action.CANCELLED
        }
    }

    protected static String getSessionUserId() {
        String sessionUserId = ((SessionUserInfo) SecurityContextHolder.context?.authentication?.principal)?.effectiveUser?.idAsString
        if (sessionUserId == null) {
            logger.warn("Unable to obtain an authentication.principal.effectiveUser.id, null somewhere?  Is spring security active?")
            throw new IllegalStateException("No session user")
        }
        sessionUserId
    }

    protected static void retrieveSpringSecurityContext(AtmosphereResource r) {
        SecurityContext context = (SecurityContext) r.session().getAttribute(SPRING__SECURITY__CONTEXT)
        if (context == null) {
            logger.warn("Was not able to obtain SPRING_SECURITY_CONTEXT.  Is Atmosphere sessionSupport active?")
            throw new IllegalStateException("No Spring Security Session")
        }
        SecurityContextHolder.context = context
    }

    @Override
    void postInspect(final AtmosphereResource r) {

    }

    @Override
    void destroy() {

    }
}
