package com.jtbdevelopment.games.mongo.dao

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Date: 12/1/14
 * Time: 10:24 PM
 */
@Component
@CompileStatic
class MongoProperties {
    @Value('${mongo.dbName:twisted}')
    String dbName;
    @Value('${mongo.host:localhost}')
    String dbHost;
    @Value('${mongo.userName:twisted}')
    String dbUser;
    @Value('${mongo.userPassword:twisted}')
    String dbPassword;
}
