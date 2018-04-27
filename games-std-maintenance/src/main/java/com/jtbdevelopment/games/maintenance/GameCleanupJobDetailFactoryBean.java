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

  private GameCleanup gameCleanup;

  @Autowired
  public void setGameCleanup(GameCleanup gameCleanup) {
    this.gameCleanup = gameCleanup;
  }

  //  Leave autowired due to circular dependency
  @PostConstruct
  public void setup() {
    setTargetObject(gameCleanup);
    setTargetMethod("deleteOlderGames");
  }
}
