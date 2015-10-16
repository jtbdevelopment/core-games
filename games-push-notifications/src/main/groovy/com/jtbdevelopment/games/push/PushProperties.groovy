package com.jtbdevelopment.games.push

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Date: 10/16/15
 * Time: 6:42 AM
 */
@CompileStatic
@Component
class PushProperties {
    @Value('${push.senderID:NOTSET}')
    String senderID
}
