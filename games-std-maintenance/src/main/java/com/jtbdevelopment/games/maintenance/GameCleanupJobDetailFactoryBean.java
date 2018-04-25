package com.jtbdevelopment.games.maintenance;

import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Date: 8/18/15 Time: 11:00 PM
 */
@Component
public class GameCleanupJobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean {

  public GameCleanupJobDetailFactoryBean(final GameCleanup gameCleanup) {
    setTargetObject(gameCleanup);
    setTargetMethod("deleteOlderGames");
  }
}
