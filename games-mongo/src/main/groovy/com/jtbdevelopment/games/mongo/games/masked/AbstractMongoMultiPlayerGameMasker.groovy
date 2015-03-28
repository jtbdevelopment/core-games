package com.jtbdevelopment.games.mongo.games.masked

import com.jtbdevelopment.games.mongo.games.AbstractMongoMultiPlayerGame
import com.jtbdevelopment.games.state.masked.AbstractMultiPlayerGameMasker
import com.jtbdevelopment.games.state.masked.MaskedMultiPlayerGame
import org.bson.types.ObjectId

/**
 * Date: 2/19/15
 * Time: 6:49 PM
 */
abstract class AbstractMongoMultiPlayerGameMasker<FEATURES, U extends AbstractMongoMultiPlayerGame<FEATURES>, M extends MaskedMultiPlayerGame<FEATURES>> extends AbstractMultiPlayerGameMasker<ObjectId, FEATURES, U, M> {
    @Override
    Class getIDClass() {
        return ObjectId.class
    }

}
