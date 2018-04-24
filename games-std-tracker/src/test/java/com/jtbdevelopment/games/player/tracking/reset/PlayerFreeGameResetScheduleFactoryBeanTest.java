package com.jtbdevelopment.games.player.tracking.reset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.impl.triggers.CronTriggerImpl;

/**
 * Date: 2/12/15 Time: 7:09 PM
 */
public class PlayerFreeGameResetScheduleFactoryBeanTest {

  @Test
  public void testInitializes() throws ParseException {
    JobKey jobKey = new JobKey("name", "group");
    JobDetail detail = Mockito.mock(JobDetail.class);
    Mockito.when(detail.getKey()).thenReturn(jobKey);
    PlayerFreeGameResetJobDetailFactoryBean job = Mockito
        .mock(PlayerFreeGameResetJobDetailFactoryBean.class);
    Mockito.when(job.getObject()).thenReturn(detail);
    PlayerFreeGameResetScheduleFactoryBean factoryBean = new PlayerFreeGameResetScheduleFactoryBean();
    factoryBean.resetJobDetailFactoryBean = job;
    factoryBean.setup();
    factoryBean.afterPropertiesSet();
    CronTriggerImpl impl = (CronTriggerImpl) factoryBean.getObject();

    assertSame(detail, impl.getJobDataMap().get("jobDetail"));
    assertEquals("0 0 0 * * ?", impl.getCronExpression());
  }

}
