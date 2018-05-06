package com.jtbdevelopment.games.mongo.state.masking;

import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractMultiPlayerGameMasker;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 2/19/15 Time: 6:51 PM
 */
public class AbstractMongoMultiPlayerGameMaskerTest {

  private AbstractMultiPlayerGameMasker masker = new AbstractMongoMultiPlayerGameMasker() {
        @Override
        protected AbstractMaskedMultiPlayerGame newMaskedGame() {
          return null;
        }

  };

  @Test
  public void testGetIDClass() {
    Assert.assertEquals(ObjectId.class, ReflectionTestUtils.invokeMethod(masker, "getIDClass"));
    }
}
