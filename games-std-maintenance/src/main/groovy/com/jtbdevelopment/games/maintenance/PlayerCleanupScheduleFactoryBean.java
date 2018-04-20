package com.jtbdevelopment.games.maintenance;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Date: 2/12/15 Time: 7:02 PM
 */
@Component
public class PlayerCleanupScheduleFactoryBean extends CronTriggerFactoryBean {

  @Autowired
  protected PlayerCleanupJobDetailFactoryBean jobDetail;

  @PostConstruct
  public void setup() {
    setJobDetail(jobDetail.getObject());
    setCronExpression("0 0 0 * * ?");
    setName("Delete Inactive Players");
  }
}
