package com.jtbdevelopment.games.maintenance

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 8/18/15
 * Time: 11:00 PM
 */
@Component
@CompileStatic
class GameCleanupJobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean {
    @Autowired
    GameCleanup gameCleanup

    @PostConstruct
    void setup() {
        setTargetObject(gameCleanup)
        setTargetMethod('deleteOlderGames')
    }
}

