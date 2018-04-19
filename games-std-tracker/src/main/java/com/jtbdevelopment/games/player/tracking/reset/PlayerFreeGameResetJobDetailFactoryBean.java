package com.jtbdevelopment.games.player.tracking.reset;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Date: 2/12/15 Time: 6:56 PM
 */
@Component
public class PlayerFreeGameResetJobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean {

  @Autowired
  protected PlayerFreeGameReset reset;

  @PostConstruct
  public void setup() {
    setTargetObject(reset);
    setTargetMethod("resetFreeGames");
  }
}
