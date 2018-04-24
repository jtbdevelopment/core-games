package com.jtbdevelopment.games.datagrid.hazelcast.cluster;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 3/4/15 Time: 10:34 PM
 */
public class UpdatesToClusterPublisherTest {

  private ITopic topic = mock(ITopic.class);
  private HazelcastInstance hazelcastInstance = mock(HazelcastInstance.class);
  private UpdatesToClusterPublisher updatesToClusterPublisher;

  @Before
  public void setup() {
    when(hazelcastInstance.getTopic(UpdatesToClusterPublisher.PUB_SUB_TOPIC))
        .thenReturn(topic);
    updatesToClusterPublisher = new UpdatesToClusterPublisher(hazelcastInstance);
  }

  @Test
  public void testSetup() {
    Assert.assertSame(topic, updatesToClusterPublisher.topic);
  }

  @Test
  public void testInternalPublish() {
    ClusterMessage m = new ClusterMessage();
    updatesToClusterPublisher.internalPublish(m);
    verify(topic).publish(m);
  }
}
