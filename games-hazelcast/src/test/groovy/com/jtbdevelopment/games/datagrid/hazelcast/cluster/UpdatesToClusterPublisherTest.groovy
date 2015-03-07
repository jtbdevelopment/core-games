package com.jtbdevelopment.games.datagrid.hazelcast.cluster

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ITopic
import com.jtbdevelopment.games.publish.cluster.ClusterMessage

/**
 * Date: 3/4/15
 * Time: 10:34 PM
 */
class UpdatesToClusterPublisherTest extends GroovyTestCase {
    UpdatesToClusterPublisher updatesToClusterPublisher = new UpdatesToClusterPublisher()

    void testSetup() {
        ITopic topic = [] as ITopic
        HazelcastInstance instance = [
                getTopic: {
                    String name ->
                        assert name == UpdatesToClusterPublisher.PUB_SUB_TOPIC
                        return topic
                }
        ] as HazelcastInstance
        updatesToClusterPublisher.hazelcastInstance = instance
        updatesToClusterPublisher.setup()
        assert updatesToClusterPublisher.topic.is(topic)
    }

    void testInternalPublish() {
        boolean published = false
        ClusterMessage m = new ClusterMessage()
        ITopic topic = [
                publish: {
                    Object o ->
                        assert o.is(m)
                        published = true
                }
        ] as ITopic
        updatesToClusterPublisher.topic = topic
        updatesToClusterPublisher.internalPublish(m)
        assert published
    }
}
