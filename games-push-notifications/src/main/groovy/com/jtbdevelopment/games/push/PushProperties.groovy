package com.jtbdevelopment.games.push

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 10/16/15
 * Time: 6:42 AM
 */
@CompileStatic
@Component
class PushProperties {
    private static final Logger logger = LoggerFactory.getLogger(PushProperties.class)

    @Value('${push.senderID:NOTSET}')
    String senderID

    @Value('${push.apiKey:NOTSET}')
    String apiKey

    boolean enabled

    @PostConstruct
    void testSetup() {
        if (apiKey == "NOTSET" || senderID == "NOTSET") {
            enabled = false
            logger.warn("------------------------------------------------------------------------------------------")
            logger.warn("------------------------------------------------------------------------------------------")
            logger.warn("push.apiKey and/or push.senderID are missing and therefore push notifications are disabled")
            logger.warn("------------------------------------------------------------------------------------------")
            logger.warn("------------------------------------------------------------------------------------------")
        } else {
            enabled = true
        }
    }
}
