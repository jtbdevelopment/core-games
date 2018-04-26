package com.jtbdevelopment.games.push.notifications;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 10/30/15 Time: 6:26 PM
 */
public class GamePublicationTrackerTest {

  private GamePublicationTracker t1 = new GamePublicationTracker();
  private GamePublicationTracker t2 = new GamePublicationTracker();
  private GamePublicationTracker t3 = new GamePublicationTracker();
  private GamePublicationTracker t4 = new GamePublicationTracker();

  @Test
  public void testEquals() {
    Assert.assertEquals(t1, t2);
    Assert.assertNotEquals(t1, t3);
    Assert.assertNotEquals(t2, t3);
    Assert.assertNotEquals(t1, t4);
    Assert.assertNotEquals(t2, t4);
  }

  @Test
  public void testHashCode() {
    Assert.assertEquals(t1.hashCode(), t2.hashCode());
    Assert.assertNotEquals(t1.hashCode(), t3.hashCode());
    Assert.assertNotEquals(t1.hashCode(), t4.hashCode());
    Assert.assertNotEquals(t2.hashCode(), t3.hashCode());
    Assert.assertNotEquals(t2.hashCode(), t4.hashCode());
  }

  @Before
  public void setup() {
    t1.setGid("GID1");
    t1.setPid("PID1");
    t2.setGid("GID1");
    t2.setPid("PID1");
    t3.setGid("GID2");
    t3.setPid("PID1");
    t4.setGid("GID1");
    t4.setPid("PID2");
  }

}
