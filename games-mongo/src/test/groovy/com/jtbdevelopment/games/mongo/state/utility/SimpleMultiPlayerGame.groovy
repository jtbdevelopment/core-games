package com.jtbdevelopment.games.mongo.state.utility

import com.jtbdevelopment.games.mongo.state.AbstractMongoMultiPlayerGame
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 1/10/15
 * Time: 2:40 PM
 */
@Document(collection = 'multi')
class SimpleMultiPlayerGame extends AbstractMongoMultiPlayerGame<String> {
    @Indexed
    int intValue
    String stringValue
}
