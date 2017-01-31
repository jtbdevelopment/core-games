package com.jtbdevelopment.games.datagrid.hazelcast.cluster

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ITopic
import com.jtbdevelopment.games.publish.cluster.AbstractUpdatesToClusterPublisher
import com.jtbdevelopment.games.publish.cluster.ClusterMessage
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 3/3/15
 * Time: 6:48 PM
 */
@Component
@CompileStatic
class UpdatesToClusterPublisher extends AbstractUpdatesToClusterPublisher {
    public static final String PUB_SUB_TOPIC = 'GAME_TOPIC'

    @Autowired
    HazelcastInstance hazelcastInstance

    ITopic topic

    @PostConstruct
    void setup() {
        topic = hazelcastInstance.getTopic(PUB_SUB_TOPIC)
    }

    @Override
    protected void internalPublish(final ClusterMessage clusterMessage) {
        topic.publish(clusterMessage)
    }

}
