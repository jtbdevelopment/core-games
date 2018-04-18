package com.jtbdevelopment.games.mongo.players;

import com.jtbdevelopment.games.players.SystemPlayer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Date: 12/30/2014 Time: 1:23 PM
 */
@Document(collection = "player")
public class MongoSystemPlayer extends MongoPlayer implements SystemPlayer<ObjectId> {

  public MongoSystemPlayer() {
    super.setSource(SYSTEM_SOURCE);
  }
}
