package com.jtbdevelopment.games.mongo.integration.games

import com.jtbdevelopment.games.mongo.games.AbstractMongoMultiPlayerGame
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 1/10/15
 * Time: 2:40 PM
 */
@Document(collection = 'multi')
class SimpleMultiPlayerGame extends AbstractMongoMultiPlayerGame {
    @Indexed
    int intValue
    String stringValue
}
