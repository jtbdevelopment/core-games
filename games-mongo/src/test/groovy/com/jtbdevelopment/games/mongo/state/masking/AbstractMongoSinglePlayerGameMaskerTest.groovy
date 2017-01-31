package com.jtbdevelopment.games.mongo.state.masking

import com.jtbdevelopment.games.state.masking.AbstractSinglePlayerGameMasker
import com.jtbdevelopment.games.state.masking.MaskedSinglePlayerGame
import org.bson.types.ObjectId

/**
 * Date: 2/19/15
 * Time: 6:51 PM
 */
class AbstractMongoSinglePlayerGameMaskerTest extends GroovyTestCase {
    AbstractSinglePlayerGameMasker masker = new AbstractMongoSinglePlayerGameMasker() {
        @Override
        protected MaskedSinglePlayerGame newMaskedGame() {
            return null
        }
    }

    void testGetIDClass() {
        assert ObjectId.class.is(masker.getIDClass())
    }
}
