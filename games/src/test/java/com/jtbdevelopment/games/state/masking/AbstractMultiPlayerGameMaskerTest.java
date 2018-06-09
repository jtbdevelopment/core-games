package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 2/19/15 Time: 6:30 PM
 */
public class AbstractMultiPlayerGameMaskerTest {

  private static IntPlayer PONE = makeSimplePlayer(1);
  private static IntPlayer PTWO = makeSimplePlayer(2);
  private MaskedIntGameMasker masker = new MaskedIntGameMasker();

  private static IntPlayer makeSimplePlayer(final Integer id, final boolean disabled) {
    IntPlayer player = new IntPlayer();

    player.setId(id);
    player.setSource("MADEUP");
    player.setSourceId("MADEUP" + id);
    player.setDisplayName(id.toString());
    player.setDisabled(disabled);
    player.setImageUrl("http://somewhere.com/image/" + id);
    player.setProfileUrl("http://somewhere.com/profile/" + id);
    return player;
  }

  private static IntPlayer makeSimplePlayer(final Integer id) {
    return AbstractMultiPlayerGameMaskerTest.makeSimplePlayer(id, false);
  }

  private static void checkUnmaskedGameFields(MaskedIntGame maskedGame, IntGame game) {
    Assert.assertEquals(game.getIdAsString(), maskedGame.getId());
    Assert.assertEquals(
        (game.getCompletedTimestamp() != null ? game.getCompletedTimestamp().toEpochMilli()
            : null),
        maskedGame.getCompletedTimestamp());
    Assert.assertEquals((game.getCreated() != null ? game.getCreated().toEpochMilli() : null),
        maskedGame.getCreated());
    Assert.assertEquals(
        (game.getDeclinedTimestamp() != null ? game.getDeclinedTimestamp().toEpochMilli()
            : null),
        maskedGame.getDeclinedTimestamp());
    Assert.assertEquals(
        (game.getRematchTimestamp() != null ? game.getRematchTimestamp().toEpochMilli()
            : null),
        maskedGame.getRematchTimestamp());
    Assert.assertEquals((game.getLastUpdate() != null ? game.getLastUpdate().toEpochMilli()
            : null),
        maskedGame.getLastUpdate());
    Assert.assertEquals(game.getFeatures(), maskedGame.getFeatures());
    Assert.assertEquals(game.getGamePhase(), maskedGame.getGamePhase());
    Assert.assertEquals(game.getRound(), maskedGame.getRound());
  }

  @Test
  public void testMaskingSinglePlayerGame() {

    Map<Features, Object> featueMap = new HashMap<>();
    featueMap.put(Features.FeatureA, "");

    LinkedHashMap<Integer, PlayerState> stateMap = new LinkedHashMap<>();
    stateMap.put(PONE.getId(), PlayerState.Accepted);

    IntGame game = new IntGame();
    game.setGamePhase(GamePhase.Quit);
    game.setPlayers(Collections.singletonList(PONE));
    game.setCreated(Instant.now());
    game.setCompletedTimestamp(Instant.now());
    game.setDeclinedTimestamp(Instant.now());
    game.setFeatures(new HashSet<>(Arrays.asList(Features.FeatureA, Features.FeatureB)));
    game.setId(101);
    game.setInitiatingPlayer(PONE.getId());
    game.setLastUpdate(Instant.now());
    game.setPlayerStates(stateMap);
    game.setVersion(10);

    MaskedIntGame maskedGame = masker.maskGameForPlayer(game, PONE);
    checkUnmaskedGameFields(maskedGame, game);

    Map<String, String> map2 = new LinkedHashMap<>(1);
    map2.put(PONE.getMd5(), PONE.getDisplayName());
    Assert.assertEquals(map2, maskedGame.getPlayers());
    Map<String, String> map3 = new LinkedHashMap<>();
    map3.put(PONE.getMd5(), PONE.getImageUrl());
    Assert.assertEquals(map3, maskedGame.getPlayerImages());
    Map<String, String> map4 = new LinkedHashMap<>();
    map4.put(PONE.getMd5(), PONE.getProfileUrl());
    Assert.assertEquals(map4, maskedGame.getPlayerProfiles());
    Assert.assertEquals(PONE.getMd5(), maskedGame.getInitiatingPlayer());
    Map<String, PlayerState> map5 = new LinkedHashMap<>();
    map5.put(PONE.getMd5(), PlayerState.Accepted);
    Assert.assertEquals(map5, maskedGame.getPlayerStates());
    Assert.assertEquals(PONE.getIdAsString(), maskedGame.getMaskedForPlayerID());
    Assert.assertEquals(PONE.md5, maskedGame.getMaskedForPlayerMD5());
  }

  private enum Features {
    FeatureA, FeatureB
  }

  private static class IntPlayer extends AbstractPlayer<Integer> {

    private String md5;
    private Integer id;

    @Override
    protected String getMd5Internal() {
      return this.md5;
    }

    @Override
    public String getIdAsString() {
      return id != null ? id.toString() : null;
    }

    public String getMd5() {
      return md5;
    }

    public void setMd5(String md5) {
      this.md5 = md5;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }
  }

  private static class IntGame extends AbstractMultiPlayerGame<Integer, Features> {

    private Integer id;
    private Integer previousId;

    @Override
    public String getIdAsString() {
      return id != null ? id.toString() : null;
    }

    @Override
    public String getPreviousIdAsString() {
      return previousId != null ? previousId.toString() : null;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public Integer getPreviousId() {
      return previousId;
    }

    public void setPreviousId(Integer previousId) {
      this.previousId = previousId;
    }
  }

  private static class MaskedIntGame extends AbstractMaskedMultiPlayerGame<Features> {

  }

  public static class MaskedIntGameMasker extends
      AbstractMultiPlayerGameMasker<Integer, Features, IntGame, MaskedIntGame> {

    @Override
    protected MaskedIntGame newMaskedGame() {
      return new MaskedIntGame();
    }

    @Override
    public Class getIDClass() {
      return Integer.class;
    }

  }
}
