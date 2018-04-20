package com.jtbdevelopment.games.maintenance;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Date: 8/18/15 Time: 11:00 PM
 */
@Component
public class GameCleanupJobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean {

  @Autowired
  protected GameCleanup gameCleanup;

  @PostConstruct
  public void setup() {
    setTargetObject(gameCleanup);
    setTargetMethod("deleteOlderGames");
  }
}
