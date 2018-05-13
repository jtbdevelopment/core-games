package com.jtbdevelopment.games.datagrid.hazelcast.cluster;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.jtbdevelopment.games.publish.cluster.AbstractUpdatesToClusterPublisher;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage;
import org.springframework.stereotype.Component;

/**
 * Date: 3/3/15 Time: 6:48 PM
 */
@Component
public class UpdatesToClusterPublisher extends AbstractUpdatesToClusterPublisher {

  static final String PUB_SUB_TOPIC = "GAME_TOPIC";
  final ITopic<ClusterMessage> topic;

  public UpdatesToClusterPublisher(HazelcastInstance hazelcastInstance) {
    topic = hazelcastInstance.getTopic(PUB_SUB_TOPIC);
  }

  @Override
  protected void internalPublish(final ClusterMessage clusterMessage) {
    topic.publish(clusterMessage);
  }
}
