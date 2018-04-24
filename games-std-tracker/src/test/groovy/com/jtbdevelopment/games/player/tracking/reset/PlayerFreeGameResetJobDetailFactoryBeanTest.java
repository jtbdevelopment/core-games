package com.jtbdevelopment.games.player.tracking.reset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 2/12/15 Time: 7:00 PM
 */
public class PlayerFreeGameResetJobDetailFactoryBeanTest {

  @Test
  public void testInitialization() {
    PlayerFreeGameReset reset = Mockito.mock(PlayerFreeGameReset.class);
    PlayerFreeGameResetJobDetailFactoryBean factoryBean = new PlayerFreeGameResetJobDetailFactoryBean();
    factoryBean.reset = reset;
    factoryBean.setup();
    assertEquals("resetFreeGames", factoryBean.getTargetMethod());
    assertSame(reset, factoryBean.getTargetObject());
  }

}
