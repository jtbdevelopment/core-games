package com.jtbdevelopment.games.mongo.games.masked

import com.jtbdevelopment.games.games.masked.AbstractMultiPlayerGameMasker
import com.jtbdevelopment.games.games.masked.MaskedMultiPlayerGame
import org.bson.types.ObjectId

/**
 * Date: 2/19/15
 * Time: 6:51 PM
 */
class AbstractMongoMultiPlayerGameMaskerTest extends GroovyTestCase {
    AbstractMultiPlayerGameMasker masker = new AbstractMongoMultiPlayerGameMasker() {
        @Override
        protected MaskedMultiPlayerGame newMaskedGame() {
            return null
        }
    }

    void testGetIDClass() {
        assert ObjectId.class.is(masker.getIDClass())
    }
}
