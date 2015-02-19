package com.jtbdevelopment.games.mongo.games.masked

import com.jtbdevelopment.games.games.masked.AbstractMultiPlayerGameMasker
import com.jtbdevelopment.games.games.masked.MaskedMultiPlayerGame
import com.jtbdevelopment.games.mongo.games.AbstractMongoMultiPlayerGame
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
