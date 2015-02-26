package com.jtbdevelopment.games.datagrid.hazelcast

import com.hazelcast.core.HazelcastInstance

/**
 * Date: 2/25/15
 * Time: 7:00 PM
 */
class HazelCastInstanceFactoryBeanTest extends GroovyTestCase {
    HazelCastInstanceFactoryBean factoryBean = new HazelCastInstanceFactoryBean()

    void testGetObject() {
        assert factoryBean.object
    }

    void testGetObjectIsSame() {
        assert factoryBean.object.is(factoryBean.object)
    }

    void testGetObjectType() {
        assert HazelcastInstance.class.is(factoryBean.objectType)
    }

    void testIsSingleton() {
        assert factoryBean.isSingleton()
    }
}
