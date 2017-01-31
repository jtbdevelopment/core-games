package com.jtbdevelopment.games.datagrid.hazelcast.cluster

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ITopic
import com.hazelcast.core.Message
import com.hazelcast.core.MessageListener
import com.jtbdevelopment.games.publish.cluster.AbstractUpdatesFromClusterListener
import com.jtbdevelopment.games.publish.cluster.ClusterMessage
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 3/3/15
 * Time: 7:51 PM
 */
@Component
@CompileStatic
class UpdatesFromClusterListener extends AbstractUpdatesFromClusterListener implements MessageListener<ClusterMessage> {

    @Autowired
    HazelcastInstance hazelcastInstance

    ITopic topic

    @PostConstruct
    void setup() {
        topic = hazelcastInstance.getTopic(UpdatesToClusterPublisher.PUB_SUB_TOPIC)
        topic.addMessageListener(this)
    }

    @Override
    void onMessage(final Message<ClusterMessage> message) {
        if (message && !message.publishingMember.localMember()) {
            ClusterMessage clusterMessage = message.messageObject
            if (clusterMessage) {
                receiveClusterMessage(clusterMessage)
            }
        }
    }
}
