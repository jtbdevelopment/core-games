package com.jtbdevelopment.games.mongo.players;

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributesFactory;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date: 12/30/2014
 * Time: 7:15 PM
 */
@Component
public class MongoPlayerFactory implements PlayerFactory<ObjectId> {

  @Autowired(required = false)
  protected GameSpecificPlayerAttributesFactory gameSpecificPlayerAttributesFactory;

  @Override
  public Player<ObjectId> newPlayer() {
    MongoPlayer player = new MongoPlayer();
    if (gameSpecificPlayerAttributesFactory != null) {
      player.setGameSpecificPlayerAttributes(
          gameSpecificPlayerAttributesFactory.newPlayerAttributes());
    }

    return player;
  }

  @Override
  public Player<ObjectId> newManualPlayer() {
    MongoManualPlayer player = new MongoManualPlayer();
    if (gameSpecificPlayerAttributesFactory != null) {
      player.setGameSpecificPlayerAttributes(
          gameSpecificPlayerAttributesFactory.newManualPlayerAttributes());
    }

    return player;
  }

  @Override
  public Player<ObjectId> newSystemPlayer() {
    MongoSystemPlayer player = new MongoSystemPlayer();
    if (gameSpecificPlayerAttributesFactory != null) {
      player.setGameSpecificPlayerAttributes(
          gameSpecificPlayerAttributesFactory.newSystemPlayerAttributes());
    }

    return player;
  }
}
