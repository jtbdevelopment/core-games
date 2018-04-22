package com.jtbdevelopment.games.mongo;

import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import org.bson.types.ObjectId;

/**
 * Date: 12/30/2014 Time: 2:00 PM
 */
public abstract class MongoGameCoreTestCase {

  protected static final MongoPlayer PONE = makeSimplePlayer("1");
  protected static final MongoPlayer PTWO = makeSimplePlayer("2");
  protected static final MongoPlayer PTHREE = makeSimplePlayer("3");
  protected static final MongoPlayer PFOUR = makeSimplePlayer("4");
  protected static final MongoPlayer PFIVE = makeSimplePlayer("5");
  protected static final MongoPlayer PINACTIVE1 = makeSimplePlayer("A1", true);
  protected static final MongoPlayer PINACTIVE2 = makeSimplePlayer("A2", true);

  protected static MongoPlayer makeSimplePlayer(String id, final boolean disabled) {
    MongoPlayer player = new MongoPlayer();

    while (id.length() < 24) {
      id = id + "0";
    }
    player.setId(new ObjectId(id));
    player.setSource("MADEUP");
    player.setSourceId("MADEUP" + id);
    player.setDisplayName(id);
    player.setDisabled(disabled);
    player.setImageUrl("http://somewhere.com/image/" + id);
    player.setProfileUrl("http://somewhere.com/profile/" + id);
    return player;
  }

  protected static MongoPlayer makeSimplePlayer(final String id) {
    return MongoGameCoreTestCase.makeSimplePlayer(id, false);
  }
}
