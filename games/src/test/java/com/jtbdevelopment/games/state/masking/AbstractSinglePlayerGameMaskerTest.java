package com.jtbdevelopment.games.state.masking;

import static org.junit.Assert.assertEquals;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Date: 2/19/15 Time: 6:30 PM
 */
public class AbstractSinglePlayerGameMaskerTest {

  private static IntPlayer PONE = makeSimplePlayer(1);
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

  protected static IntPlayer makeSimplePlayer(final Integer id) {
    return AbstractSinglePlayerGameMaskerTest.makeSimplePlayer(id, false);
  }

  private static void checkUnmaskedGameFields(MaskedIntGame maskedGame, IntGame game) {
    assertEquals(game.getIdAsString(), maskedGame.getId());
    assertEquals(game.getPreviousIdAsString(), maskedGame.getPreviousId());
    assertEquals(
        (game.getCompletedTimestamp() != null ? game.getCompletedTimestamp().toEpochMilli()
            : null),
        maskedGame.getCompletedTimestamp());
    assertEquals((game.getCreated() != null ? game.getCreated().toEpochMilli() : null),
        maskedGame.getCreated());
    assertEquals((game.getLastUpdate() != null ? game.getLastUpdate().toEpochMilli()
            : null),
        maskedGame.getLastUpdate());
    assertEquals(game.getFeatures(), maskedGame.getFeatures());
    assertEquals(game.getGamePhase(), maskedGame.getGamePhase());
    assertEquals(game.getRound(), maskedGame.getRound());
  }

  @Test
  public void testMaskingSinglePlayerGame() {

    Map<Features, Object> featureMap = new LinkedHashMap<>();
    featureMap.put(Features.FeatureA, "");

    IntGame game = new IntGame();
    game.setGamePhase(GamePhase.Quit);
    game.setPlayer(PONE);
    game.setCreated(Instant.now());
    game.setCompletedTimestamp(Instant.now());
    game.setFeatures(new HashSet<>(Arrays.asList(Features.FeatureA, Features.FeatureB)));
    game.setPreviousId(100);
    game.setId(101);
    game.setLastUpdate(Instant.now());
    game.setVersion(10);

    MaskedIntGame maskedGame = masker.maskGameForPlayer(game, PONE);
    checkUnmaskedGameFields(maskedGame, game);

    LinkedHashMap<String, String> map1 = new LinkedHashMap<String, String>(1);
    map1.put(PONE.getMd5(), PONE.getDisplayName());
    assertEquals(map1, maskedGame.getPlayers());
    LinkedHashMap<String, String> map2 = new LinkedHashMap<String, String>(1);
    map2.put(PONE.getMd5(), PONE.getImageUrl());
    assertEquals(map2, maskedGame.getPlayerImages());
    LinkedHashMap<String, String> map3 = new LinkedHashMap<String, String>(1);
    map3.put(PONE.getMd5(), PONE.getProfileUrl());
    assertEquals(map3, maskedGame.getPlayerProfiles());
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

  private static class IntGame extends AbstractSinglePlayerGame<Integer, Features> {

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

  private static class MaskedIntGame extends AbstractMaskedSinglePlayerGame<Features> {

  }

  public static class MaskedIntGameMasker extends
      AbstractSinglePlayerGameMasker<Integer, Features, IntGame, MaskedIntGame> {

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
