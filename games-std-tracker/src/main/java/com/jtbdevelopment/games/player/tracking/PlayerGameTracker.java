package com.jtbdevelopment.games.player.tracking;

import com.jtbdevelopment.games.dao.caching.CacheConstants;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.SystemPlayer;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Date: 2/4/15 Time: 7:02 PM
 */
@Component
public class PlayerGameTracker implements GameEligibilityTracker<PlayerGameEligibilityResult> {

  private static final Update UPDATE_FREE_GAMES = new Update()
      .inc(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD, 1);
  private static final Update REVERT_FREE_GAMES = new Update()
      .inc(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD, -1);
  private static final Update UPDATE_PAID_GAMES = new Update()
      .inc(AbstractPlayerGameTrackingAttributes.PAID_GAMES_FIELD, -1);
  private static final Update REVERT_PAID_GAMES = new Update()
      .inc(AbstractPlayerGameTrackingAttributes.PAID_GAMES_FIELD, 1);
  private static final FindAndModifyOptions RETURN_NEW_OPTION = new FindAndModifyOptions()
      .returnNew(true);
  private final MongoOperations mongoOperations;
  private final PlayerPublisher playerPublisher;

  PlayerGameTracker(
      final MongoOperations mongoOperations,
      final PlayerPublisher playerPublisher) {
    this.mongoOperations = mongoOperations;
    this.playerPublisher = playerPublisher;
  }

  /**
   * Checks eligibility for player and decrements appropriate value Caller should retain value until
   * all actions completed in case rollback is needed
   *
   * @return result that can be checked if player is eligible and to pass in case reverting needed
   */
  @Caching(evict = {@CacheEvict(value = CacheConstants.PLAYER_ID_CACHE, key = "#result.player.id"),
      @CacheEvict(value = CacheConstants.PLAYER_MD5_CACHE, key = "#result.player.md5"),
      @CacheEvict(value = CacheConstants.PLAYER_S_AND_SID_CACHE, key = "#result.player.sourceAndSourceId")})
  @Override
  public PlayerGameEligibilityResult getGameEligibility(final Player player) {
    if (player instanceof SystemPlayer) {
      PlayerGameEligibilityResult result = new PlayerGameEligibilityResult();
      result.setEligibility(PlayerGameEligibility.SystemPlayer);
      result.setPlayer(player);
      return result;
    }

    int freeGames = ((AbstractPlayerGameTrackingAttributes) player
        .getGameSpecificPlayerAttributes()).getMaxDailyFreeGames();

    //  Try free game first
    MongoPlayer updated = (MongoPlayer) mongoOperations.findAndModify(Query.query(
        Criteria.where("_id").is(player.getId())
            .and(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD).lt(freeGames)),
        UPDATE_FREE_GAMES, RETURN_NEW_OPTION, player.getClass());
    if (updated != null) {
      playerPublisher.publish(updated);
      PlayerGameEligibilityResult result = new PlayerGameEligibilityResult();
      result.setEligibility(PlayerGameEligibility.FreeGameUsed);
      result.setPlayer(updated);
      return result;
    }

    updated = (MongoPlayer) mongoOperations.findAndModify(Query.query(
        Criteria.where("_id").is(player.getId())
            .and(AbstractPlayerGameTrackingAttributes.PAID_GAMES_FIELD).gt(0)), UPDATE_PAID_GAMES,
        RETURN_NEW_OPTION, player.getClass());

    if (updated != null) {
      playerPublisher.publish(updated);
      PlayerGameEligibilityResult result = new PlayerGameEligibilityResult();
      result.setEligibility(PlayerGameEligibility.PaidGameUsed);
      result.setPlayer(updated);
      return result;
    }

    PlayerGameEligibilityResult result = new PlayerGameEligibilityResult();
    result.setEligibility(PlayerGameEligibility.NoGamesAvailable);
    result.setPlayer(player);
    return result;
  }

  /**
   * Revert the result of getGameEligibility
   *
   * @param gameEligibilityResult returned from getGameEligibility
   */
  @Caching(evict = {@CacheEvict(value = CacheConstants.PLAYER_ID_CACHE, key = "#p0.player.id"),
      @CacheEvict(value = CacheConstants.PLAYER_MD5_CACHE, key = "#p0.player.md5"),
      @CacheEvict(value = CacheConstants.PLAYER_S_AND_SID_CACHE, key = "#p0.player.sourceAndSourceId")})
  @Override
  public void revertGameEligibility(final PlayerGameEligibilityResult gameEligibilityResult) {
    Update revertToUse = null;
    switch (gameEligibilityResult.getEligibility()) {
      case FreeGameUsed:
        revertToUse = REVERT_FREE_GAMES;
        break;
      case PaidGameUsed:
        revertToUse = REVERT_PAID_GAMES;
        break;
    }

    if (revertToUse != null) {
      MongoPlayer updated = (MongoPlayer) mongoOperations.findAndModify(
          Query.query(Criteria.where("_id").is(gameEligibilityResult.getPlayer().getId())),
          revertToUse, RETURN_NEW_OPTION,
          gameEligibilityResult.getPlayer().getClass());
      if (updated != null) {
        playerPublisher.publish(updated);
      }

    }

  }
}
