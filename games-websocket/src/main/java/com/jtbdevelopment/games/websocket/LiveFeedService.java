package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.websocket.WebSocketMessage.MessageType;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.PathParam;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedService(path = "/livefeed/{id: [a-zA-Z][a-zA-Z_0-9]*}", interceptors = {
    SpringSecuritySessionInterceptor.class, AtmosphereResourceLifecycleInterceptor.class,
    TrackMessageSizeInterceptor.class, SuspendTrackerInterceptor.class,
    HeartbeatInterceptor.class}, atmosphereConfig = {"supportSession=true"})
public class LiveFeedService {

  public static final String PATH_ROOT = "/livefeed/";
  private static final Logger logger = LoggerFactory.getLogger(LiveFeedService.class);
  @PathParam("id")
  public String id;

  public LiveFeedService() {
    logger.info("LiveFeedService instantiated");
  }

  @Ready(encoders = {WebSocketJSONConverter.class})
  public WebSocketMessage onReady(final AtmosphereResource r) {
    logger.info("Browser {} connected to pathParam id {}.", r.uuid(), id);
    WebSocketMessage message = new WebSocketMessage();
    message.setMessageType(MessageType.Heartbeat);
    message.setMessage("connected to " + id);
    return message;
  }

  @Disconnect
  public void onDisconnect(final AtmosphereResourceEvent event) {
    if (event.isCancelled()) {
      // We didn't get notified, so we remove the user.
      logger.info("Browser {} unexpectedly disconnected for pathParam {}.",
          event.getResource().uuid(),
          id);
    } else if (event.isClosedByClient()) {
      logger.info("Browser {} closed the connection for pathParam {}.",
          event.getResource().uuid(),
          id);
    } else {
      logger.info("Browser {} closed for other reason for pathParam {}.",
          event.getResource().uuid(),
          id);
    }

  }

  @Message(decoders = {WebSocketJSONConverter.class}, encoders = {WebSocketJSONConverter.class})
  public WebSocketMessage onMessage(WebSocketMessage message) {
    return message;
  }
}
