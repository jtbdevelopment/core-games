package com.jtbdevelopment.games.security.spring.social.facebook

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 12/13/14
 * Time: 9:03 PM
 */
@CompileStatic
@Component
class FacebookProperties {
    private static final Logger logger = LoggerFactory.getLogger(FacebookProperties.class)
    @Value('${facebook.clientID:NOTSET}')
    String clientID;
    @Value('${facebook.clientSecret:NOTSET}')
    String clientSecret;

    @PostConstruct
    public void testDefaults() {
        if (clientID == 'NOTSET' || clientSecret == 'NOTSET') {
            logger.warn('----------------------------------------------------------------------------------------------')
            logger.warn('----------------------------------------------------------------------------------------------')
            logger.warn('----------------------------------------------------------------------------------------------')
            logger.warn('facebook.clientID AND/OR facebook.clientSecret is using default values.  Not likely to work!!!')
            logger.warn('----------------------------------------------------------------------------------------------')
            logger.warn('----------------------------------------------------------------------------------------------')
            logger.warn('----------------------------------------------------------------------------------------------')
        }
    }
}
