package com.jtbdevelopment.games.player.tracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.mongo.players.MongoSystemPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerPayLevel;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Date: 4/9/15 Time: 9:29 AM
 */
public class PlayerGameTrackerTest extends MongoGameCoreTestCase {

  private static int DEFAULT_FREE = 22;
  private static int DEFAULT_PREMIUM = 37;
  private MongoPlayer input = MongoGameCoreTestCase.makeSimplePlayer("34");
  private MongoPlayer output = MongoGameCoreTestCase.makeSimplePlayer(input.getIdAsString());
  private Query QUERY_AVAILABLE_FTP = Query.query(Criteria.where("_id").is(input.getId())
      .and(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD).lt(DEFAULT_FREE));
  private Query QUERY_AVAILABLE_PREMIUM_FTP = Query.query(Criteria.where("_id").is(input.getId())
      .and(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD).lt(DEFAULT_PREMIUM));
  private Query QUERY_AVAILABLE_PAID = Query.query(Criteria.where("_id").is(input.getId())
      .and(AbstractPlayerGameTrackingAttributes.PAID_GAMES_FIELD).gt(0));
  private Query QUERY_PLAYER = Query.query(Criteria.where("_id").is(input.getId()));
  private Update UPDATE_FREE = new Update()
      .inc(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD, 1);
  private Update UPDATE_PAID = new Update()
      .inc(AbstractPlayerGameTrackingAttributes.PAID_GAMES_FIELD, -1);
  private Update REVERT_FREE_GAMES = new Update()
      .inc(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD, -1);
  private Update REVERT_PAID_GAMES = new Update()
      .inc(AbstractPlayerGameTrackingAttributes.PAID_GAMES_FIELD, 1);
  private MongoOperations mongoOperations = Mockito.mock(MongoOperations.class);
  private PlayerPublisher playerPublisher = Mockito.mock(PlayerPublisher.class);
  private PlayerGameTracker tracker = new PlayerGameTracker(mongoOperations, playerPublisher);

  @Test
  public void testSystemPlayer() {
    MongoSystemPlayer input = new MongoSystemPlayer();
    PlayerGameEligibilityResult result = tracker.getGameEligibility(input);
    assertNotNull(result);
    assertEquals(PlayerGameEligibility.SystemPlayer, result.getEligibility());
    assertSame(input, result.getPlayer());
  }

  @Test
  public void testRegularEligibilityWithPlayerHavingFreeGames() {
    input.setPayLevel(PlayerPayLevel.FreeToPlay);
    PlayerAttributes attributes = new PlayerAttributes();
    attributes.setAvailablePurchasedGames(5);
    attributes.setPlayer(input);
    attributes.setFreeGamesUsedToday(0);
    input.setGameSpecificPlayerAttributes(attributes);

    when(mongoOperations.findAndModify(
        Matchers.eq(QUERY_AVAILABLE_FTP),
        Matchers.eq(UPDATE_FREE),
        Matchers.isA(FindAndModifyOptions.class),
        Matchers.eq(MongoPlayer.class)))
        .thenReturn(output);
    PlayerGameEligibilityResult result = tracker.getGameEligibility(input);
    assertNotNull(result);
    assertEquals(PlayerGameEligibility.FreeGameUsed, result.getEligibility());
    assertSame(output, result.getPlayer());
    verify(playerPublisher).publish(output);
  }

  @Test
  public void testPremiumEligibilityWithPlayerHavingFreeGames() {
    input.setPayLevel(PlayerPayLevel.PremiumPlayer);
    PlayerAttributes attributes = new PlayerAttributes();
    attributes.setFreeGamesUsedToday(0);
    attributes.setAvailablePurchasedGames(5);
    input.setGameSpecificPlayerAttributes(attributes);
    when(mongoOperations.findAndModify(
        Matchers.eq(QUERY_AVAILABLE_PREMIUM_FTP),
        Matchers.eq(UPDATE_FREE),
        Matchers.isA(FindAndModifyOptions.class),
        Matchers.eq(MongoPlayer.class)))
        .thenReturn(output);
    PlayerGameEligibilityResult result = tracker.getGameEligibility(input);
    assertNotNull(result);
    assertEquals(PlayerGameEligibility.FreeGameUsed, result.getEligibility());
    assertSame(output, result.getPlayer());
    verify(playerPublisher).publish(output);
  }

  @Test
  public void testRegularEligibilityWithPlayerHavingNoFreeGamesButHasPaid() {
    input.setPayLevel(PlayerPayLevel.FreeToPlay);
    PlayerAttributes attributes = new PlayerAttributes();
    attributes.setFreeGamesUsedToday(10);
    attributes.setAvailablePurchasedGames(5);
    input.setGameSpecificPlayerAttributes(attributes);
    when(mongoOperations.findAndModify(
        Matchers.eq(QUERY_AVAILABLE_FTP),
        Matchers.eq(UPDATE_FREE),
        Matchers.isA(FindAndModifyOptions.class),
        Matchers.eq(input.getClass())))
        .thenReturn(null);
    when(mongoOperations
        .findAndModify(
            Matchers.eq(QUERY_AVAILABLE_PAID),
            Matchers.eq(UPDATE_PAID),
            Matchers.isA(FindAndModifyOptions.class),
            Matchers.eq(MongoPlayer.class)))
        .thenReturn(output);
    PlayerGameEligibilityResult result = tracker.getGameEligibility(input);
    assertNotNull(result);
    assertEquals(PlayerGameEligibility.PaidGameUsed, result.getEligibility());
    assertSame(output, result.getPlayer());
    verify(playerPublisher).publish(output);
  }

  @Test
  public void testRegularEligibilityWithPlayerHavingNoGames() {
    input.setPayLevel(PlayerPayLevel.FreeToPlay);
    PlayerAttributes attributes = new PlayerAttributes();
    attributes.setFreeGamesUsedToday(10);
    attributes.setAvailablePurchasedGames(0);
    input.setGameSpecificPlayerAttributes(attributes);
    when(mongoOperations.findAndModify(
        Matchers.eq(QUERY_AVAILABLE_FTP),
        Matchers.eq(UPDATE_FREE),
        Matchers.isA(FindAndModifyOptions.class),
        Matchers.eq(input.getClass())))
        .thenReturn(null);
    when(mongoOperations.findAndModify(
        Matchers.eq(QUERY_AVAILABLE_PAID),
        Matchers.eq(UPDATE_PAID),
        Matchers.isA(FindAndModifyOptions.class),
        Matchers.eq(input.getClass())))
        .thenReturn(null);
    PlayerGameEligibilityResult result = tracker.getGameEligibility(input);
    assertNotNull(result);
    assertEquals(PlayerGameEligibility.NoGamesAvailable, result.getEligibility());
    assertSame(input, result.getPlayer());
    verify(playerPublisher, Mockito.never()).publish(output);
  }

  @Test
  public void testRevertingFreeGameUsage() {
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.FreeGameUsed);
    eligibilityResult.setPlayer(input);
    when(mongoOperations.findAndModify(
        Matchers.eq(QUERY_PLAYER),
        Matchers.eq(REVERT_FREE_GAMES),
        Matchers.isA(FindAndModifyOptions.class),
        Matchers.eq(MongoPlayer.class)))
        .thenReturn(output);
    tracker.revertGameEligibility(eligibilityResult);
    verify(playerPublisher).publish(output);
  }

  @Test
  public void testRevertingPaidGameUsage() {
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.PaidGameUsed);
    eligibilityResult.setPlayer(input);
    when(mongoOperations.findAndModify(
        Matchers.eq(QUERY_PLAYER),
        Matchers.eq(REVERT_PAID_GAMES),
        Matchers.isA(FindAndModifyOptions.class),
        Matchers.eq(MongoPlayer.class)))
        .thenReturn(output);
    tracker.revertGameEligibility(eligibilityResult);
    verify(playerPublisher).publish(output);
  }

  @Test
  public void testRevertingNotEligibleGameDoesNothing() {
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.NoGamesAvailable);
    eligibilityResult.setPlayer(input);
    tracker.revertGameEligibility(eligibilityResult);
    verify(mongoOperations, Mockito.never())
        .findAndModify(Matchers.isA(Query.class), Matchers.isA(Update.class),
            Matchers.isA(FindAndModifyOptions.class), Matchers.isA(Class.class));
    verify(playerPublisher, Mockito.never()).publish(output);
  }

  private static class PlayerAttributes extends AbstractPlayerGameTrackingAttributes {

    private int maxDailyFreeGames;

    @Override
    public void setPlayer(final Player player) {
      super.setPlayer(player);
      maxDailyFreeGames = (player.getPayLevel().equals(PlayerPayLevel.FreeToPlay)
          ? DEFAULT_FREE : DEFAULT_PREMIUM);
    }

    public int getMaxDailyFreeGames() {
      return maxDailyFreeGames;
    }

    public void setMaxDailyFreeGames(int maxDailyFreeGames) {
      this.maxDailyFreeGames = maxDailyFreeGames;
    }
  }
}
