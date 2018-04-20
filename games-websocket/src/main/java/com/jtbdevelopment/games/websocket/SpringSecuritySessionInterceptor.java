package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.security.SessionUserInfo;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Date: 12/18/14 Time: 6:53 PM
 */
public class SpringSecuritySessionInterceptor implements AtmosphereInterceptor {

  public static final String SPRING__SECURITY__CONTEXT = "SPRING_SECURITY_CONTEXT";
  private static final Logger logger = LoggerFactory
      .getLogger(SpringSecuritySessionInterceptor.class);

  private static String getSessionUserId() {
    final SecurityContext context = SecurityContextHolder.getContext();
    final Authentication authentication = (context == null ? null : context.getAuthentication());
    final SessionUserInfo info = (SessionUserInfo) (authentication == null ? null
        : authentication.getPrincipal());
    final Player user = (info == null ? null : info.getEffectiveUser());
    String sessionUserId = (user == null ? null : user.getIdAsString());
    if (sessionUserId == null) {
      logger.warn(
          "Unable to obtain an authentication.principal.effectiveUser.id, null somewhere?  Is spring security active?");
      throw new IllegalStateException("No session user");
    }

    return sessionUserId;
  }

  private static void retrieveSpringSecurityContext(AtmosphereResource r) {
    SecurityContext context = (SecurityContext) r.session().getAttribute(SPRING__SECURITY__CONTEXT);
    if (context == null) {
      logger.warn(
          "Was not able to obtain SPRING_SECURITY_CONTEXT.  Is Atmosphere sessionSupport active?");
      throw new IllegalStateException("No Spring Security Session");
    }

    SecurityContextHolder.setContext(context);
  }

  @Override
  public void configure(final AtmosphereConfig config) {

  }

  @Override
  public Action inspect(final AtmosphereResource r) {
    try {
      retrieveSpringSecurityContext(r);

      AtmosphereRequest request = r.getRequest();
      String requestUserId = request.getPathInfo();
      String expectedRequestPath = "/" + getSessionUserId();
      if (!requestUserId.equals(expectedRequestPath)) {
        logger.warn("INVALID REQUEST FOR " + requestUserId + " FROM " + getSessionUserId());
        return Action.CANCELLED;
      }

      return Action.CONTINUE;
    } catch (Exception e) {
      logger.warn("Exception determining Security of Atmosphere Session", e);
      return Action.CANCELLED;
    }

  }

  @Override
  public void postInspect(final AtmosphereResource r) {

  }

  @Override
  public void destroy() {

  }
}
