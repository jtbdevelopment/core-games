package com.jtbdevelopment.games.websocket;

import org.atmosphere.cpr.Universe;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

/**
 * Date: 6/4/15 Time: 12:15 AM
 */
@Component
public class AtmosphereLifecycle implements Lifecycle {

  @Override
  public void start() {

  }

  @Override
  public void stop() {
    Universe.framework().destroy();
  }

  @Override
  public boolean isRunning() {
    return false;
  }

}
