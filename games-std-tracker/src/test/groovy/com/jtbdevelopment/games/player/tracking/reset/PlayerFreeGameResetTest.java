package com.jtbdevelopment.games.player.tracking.reset;

import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.player.tracking.AbstractPlayerGameTrackingAttributes;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Date: 2/11/15 Time: 7:16 PM
 */
public class PlayerFreeGameResetTest {

  private MongoOperations operations = Mockito.mock(MongoOperations.class);
  private PlayerPublisher playerPublisher = Mockito.mock(PlayerPublisher.class);
  private PlayerFreeGameReset freeGameReset = new PlayerFreeGameReset();

  @Test
  public void testResetsAndPublishes() {
    Query q = Query
        .query(Criteria.where(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD).gt(0));
    Update u = Update.update(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD, 0);
    Mockito.when(
        operations.updateMulti(Matchers.eq(q), Matchers.eq(u), Matchers.eq(MongoPlayer.class)))
        .thenReturn(null);
    freeGameReset.mongoOperations = operations;
    freeGameReset.playerPublisher = playerPublisher;
    freeGameReset.resetFreeGames();
    Mockito.verify(operations)
        .updateMulti(Matchers.eq(q), Matchers.eq(u), Matchers.eq(MongoPlayer.class));
    Mockito.verify(playerPublisher).publishAll();
  }
}
