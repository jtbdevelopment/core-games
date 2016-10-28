package com.jtbdevelopment.games.rest.exceptions

import org.glassfish.jersey.server.monitoring.ApplicationEvent
import org.glassfish.jersey.server.monitoring.ApplicationEventListener
import org.glassfish.jersey.server.monitoring.RequestEvent
import org.glassfish.jersey.server.monitoring.RequestEventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.ext.Provider

/**
 * Date: 10/2/16
 * Time: 8:22 AM
 *
 * TODO - should this be in core common?  There is no jersey core for now, so no
 */
@Provider
class ExceptionListenerLogger implements ApplicationEventListener, RequestEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionListenerLogger.class)

    void onEvent(final RequestEvent event) {
        if (event.type == RequestEvent.Type.ON_EXCEPTION) {
            logger.error("REST Exception", event.exception)
        }
    }

    void onEvent(final ApplicationEvent event) {
    }

    RequestEventListener onRequest(final RequestEvent requestEvent) {
        return this
    }
}
