package com.jtbdevelopment.games.security.spring.redirects

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Date: 6/13/15
 * Time: 7:55 PM
 */
@CompileStatic
@Component
class MobileAppProperties {
    @Value('${mobile.success.url:#/app/games}')
    String mobileSuccessUrl

    @Value('${mobile.failure.url:#/app/signin}')
    String mobileFailureUrl
}
