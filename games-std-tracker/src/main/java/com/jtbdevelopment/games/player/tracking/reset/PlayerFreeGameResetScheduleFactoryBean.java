package com.jtbdevelopment.games.player.tracking.reset;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Date: 2/12/15 Time: 7:02 PM
 */
@Component
public class PlayerFreeGameResetScheduleFactoryBean extends CronTriggerFactoryBean {

  @Autowired
  protected PlayerFreeGameResetJobDetailFactoryBean resetJobDetailFactoryBean;

  @PostConstruct
  public void setup() {
    setJobDetail(resetJobDetailFactoryBean.getObject());
    setCronExpression("0 0 0 * * ?");
    setName("Reset Free Games");
  }
}
