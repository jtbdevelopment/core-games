package com.jtbdevelopment.games.mongo.dao

import com.jtbdevelopment.games.mongo.dao.converters.MongoConverter
import com.mongodb.Mongo
import com.mongodb.WriteConcern
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.authentication.UserCredentials
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.convert.CustomConversions
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * Date: 2/23/14
 * Time: 6:17 PM
 */
@Configuration
@EnableMongoRepositories("com.jtbdevelopment")
@CompileStatic
class MongoConfiguration extends AbstractMongoConfiguration {
    @Autowired
    List<MongoConverter> mongoConverters
    @Autowired
    MongoProperties mongoProperties

    @Override
    protected String getDatabaseName() {
        return mongoProperties.dbName
    }

    @Override
    protected String getMappingBasePackage() {
        return "com.jtbdevelopment"
    }

    @Override
    protected UserCredentials getUserCredentials() {
        return new UserCredentials(mongoProperties.dbUser, mongoProperties.dbPassword)
    }

    @Override
    CustomConversions customConversions() {
        return new CustomConversions(mongoConverters)
    }

    @Override
    Mongo mongo() throws Exception {
        Mongo mongo = new Mongo(mongoProperties.dbHost)
        mongo.setWriteConcern(WriteConcern.JOURNALED)
        return mongo
    }
}
