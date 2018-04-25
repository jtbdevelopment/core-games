package com.jtbdevelopment.games.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Date: 2/12/15 Time: 7:02 PM
 */
@Component
public class GameCleanupScheduleFactoryBean extends CronTriggerFactoryBean {

  @Autowired
  public GameCleanupScheduleFactoryBean(
      final GameCleanupJobDetailFactoryBean jobDetail) {
    setJobDetail(jobDetail.getObject());
    setCronExpression("0 0 0 * * ?");
    setName("Delete Old Games");
  }
}
