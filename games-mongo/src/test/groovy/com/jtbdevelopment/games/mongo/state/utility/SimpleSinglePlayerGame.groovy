package com.jtbdevelopment.games.mongo.state.utility

import com.jtbdevelopment.games.mongo.state.AbstractMongoSinglePlayerGame
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 1/10/15
 * Time: 2:36 PM
 */
@Document(collection = 'single')
class SimpleSinglePlayerGame extends AbstractMongoSinglePlayerGame<String> {
    @Indexed
    int intValue
    String stringValue
}
