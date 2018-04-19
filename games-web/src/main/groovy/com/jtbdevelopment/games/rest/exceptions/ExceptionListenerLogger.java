package com.jtbdevelopment.games.rest.exceptions;

import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEvent.Type;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 10/2/16 Time: 8:22 AM
 *
 * TODO - should this be in core common?  There is no jersey core for now, so no
 */
@Provider
public class ExceptionListenerLogger implements ApplicationEventListener, RequestEventListener {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionListenerLogger.class);

  public void onEvent(final RequestEvent event) {
    if (event.getType().equals(Type.ON_EXCEPTION)) {
      logger.error("REST Exception", event.getException());
    }

  }

  public void onEvent(final ApplicationEvent event) {
  }

  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return this;
  }
}
