package com.jtbdevelopment.games.mongo.state.masking;

import com.jtbdevelopment.games.state.masking.AbstractSinglePlayerGameMasker;
import com.jtbdevelopment.games.state.masking.MaskedSinglePlayerGame;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 2/19/15 Time: 6:51 PM
 */
public class AbstractMongoSinglePlayerGameMaskerTest {

    private AbstractSinglePlayerGameMasker masker = new AbstractMongoSinglePlayerGameMasker() {
        @Override
        protected MaskedSinglePlayerGame newMaskedGame() {
            return null;
        }

    };

    @Test
    public void testGetIDClass() {
        Assert.assertEquals(ObjectId.class, ReflectionTestUtils.invokeMethod(masker, "getIDClass"));
    }
}
