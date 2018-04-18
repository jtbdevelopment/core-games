package com.jtbdevelopment.games.mongo.json

import com.fasterxml.jackson.databind.module.SimpleModule
import com.jtbdevelopment.core.spring.jackson.JacksonModuleCustomization
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 2/8/15
 * Time: 3:46 PM
 */
@Component
@CompileStatic
class MongoPlayerJacksonRegistration implements JacksonModuleCustomization {
    @Override
    void customizeModule(final SimpleModule module) {
        module.addAbstractTypeMapping(Player.class, MongoPlayer.class)
    }
}
