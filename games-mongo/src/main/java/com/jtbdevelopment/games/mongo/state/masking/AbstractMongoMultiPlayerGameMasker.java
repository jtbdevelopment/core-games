package com.jtbdevelopment.games.mongo.state.masking;

import com.jtbdevelopment.games.mongo.state.AbstractMongoMultiPlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractMultiPlayerGameMasker;
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame;
import org.bson.types.ObjectId;

/**
 * Date: 2/19/15
 * Time: 6:49 PM
 */
public abstract class AbstractMongoMultiPlayerGameMasker<FEATURES, U extends AbstractMongoMultiPlayerGame<FEATURES>, M extends MaskedMultiPlayerGame<FEATURES>>
    extends AbstractMultiPlayerGameMasker<ObjectId, FEATURES, U, M> {

  @Override
  public Class<ObjectId> getIDClass() {
    return ObjectId.class;
  }

}
