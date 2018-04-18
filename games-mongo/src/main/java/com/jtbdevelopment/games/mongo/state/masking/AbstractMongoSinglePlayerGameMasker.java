package com.jtbdevelopment.games.mongo.state.masking;

import com.jtbdevelopment.games.mongo.state.AbstractMongoSinglePlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractSinglePlayerGameMasker;
import com.jtbdevelopment.games.state.masking.MaskedSinglePlayerGame;
import org.bson.types.ObjectId;

/**
 * Date: 2/19/15
 * Time: 6:49 PM
 */
public abstract class AbstractMongoSinglePlayerGameMasker<FEATURES, U extends AbstractMongoSinglePlayerGame<FEATURES>, M extends MaskedSinglePlayerGame<FEATURES>>
    extends AbstractSinglePlayerGameMasker<ObjectId, FEATURES, U, M> {

  @Override
  public Class<ObjectId> getIDClass() {
    return ObjectId.class;
  }

}
