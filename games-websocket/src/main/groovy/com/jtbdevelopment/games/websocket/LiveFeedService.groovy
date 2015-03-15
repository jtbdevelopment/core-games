package com.jtbdevelopment.games.websocket

import groovy.transform.CompileStatic
import org.atmosphere.client.TrackMessageSizeInterceptor
import org.atmosphere.config.service.*
import org.atmosphere.cpr.AtmosphereResource
import org.atmosphere.cpr.AtmosphereResourceEvent
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor
import org.atmosphere.interceptor.SuspendTrackerInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ManagedService(
        path = "/livefeed/{id: [a-zA-Z][a-zA-Z_0-9]*}",
        interceptors = [
                SpringSecuritySessionInterceptor.class,
                AtmosphereResourceLifecycleInterceptor.class,
                TrackMessageSizeInterceptor.class,
                SuspendTrackerInterceptor.class
        ],
        atmosphereConfig = ["supportSession=true"]
)
@CompileStatic
public class LiveFeedService {

    static final Logger logger = LoggerFactory.getLogger(LiveFeedService.class)
    static final String PATH_ROOT = "/livefeed/"

    @PathParam("id")
    String id

    public LiveFeedService() {
        logger.info("LiveFeedService instantiated")
    }

    @Ready(encoders = [WebSocketJSONConverter.class])
    public WebSocketMessage onReady(final AtmosphereResource r) {
        logger.info("Browser {} connected to pathParam id {}.", r.uuid(), id);
        return new WebSocketMessage(messageType: WebSocketMessage.MessageType.Heartbeat, message: "connected to " + id)
    }

    @SuppressWarnings(["GroovyUnusedDeclaration", "GrMethodMayBeStatic"])
    @Disconnect
    public void onDisconnect(final AtmosphereResourceEvent event) {
        if (event.cancelled) {
            // We didn't get notified, so we remove the user.
            logger.info("Browser {} unexpectedly disconnected for pathParam {}.", event.resource.uuid(), id)
        } else if (event.closedByClient) {
            logger.info("Browser {} closed the connection for pathParam {}.", event.resource.uuid(), id)
        } else {
            logger.info("Browser {} closed for other reason for pathParam {}.", event.resource.uuid(), id)
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    @Message(decoders = [WebSocketJSONConverter.class], encoders = [WebSocketJSONConverter.class])
    public WebSocketMessage onMessage(WebSocketMessage message) {
        return message
    }
}