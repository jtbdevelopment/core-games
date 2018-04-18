package com.jtbdevelopment.games.datagrid.hazelcast.cluster;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.jtbdevelopment.games.publish.cluster.AbstractUpdatesFromClusterListener;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage;
import javax.annotation.PostConstruct;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date: 3/3/15 Time: 7:51 PM
 */
@Component
public class UpdatesFromClusterListener extends AbstractUpdatesFromClusterListener implements
    MessageListener<ClusterMessage> {

  @Autowired
  protected HazelcastInstance hazelcastInstance;
  private ITopic topic;

  @PostConstruct
  public void setup() {
    topic = hazelcastInstance.getTopic(UpdatesToClusterPublisher.PUB_SUB_TOPIC);
    topic.addMessageListener(this);
  }

  @Override
  public void onMessage(final Message<ClusterMessage> message) {
    if (message != null && !message.getPublishingMember().localMember()) {
      ClusterMessage clusterMessage = message.getMessageObject();
      if (DefaultGroovyMethods.asBoolean(clusterMessage)) {
        receiveClusterMessage(clusterMessage);
      }

    }

  }
}
