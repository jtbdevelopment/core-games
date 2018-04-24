package com.jtbdevelopment.games;

import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.SystemPlayer;
import com.jtbdevelopment.games.stringimpl.StringGame;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringManualPlayer;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import com.jtbdevelopment.games.stringimpl.StringSystemPlayer;

/**
 * Date: 11/8/14 Time: 9:09 AM
 */
public abstract class GameCoreTestCase {

  public static final Player<String> PONE = makeSimplePlayer("1");
  public static final Player<String> PTWO = makeSimplePlayer("2");
  public static final Player<String> PTHREE = makeSimplePlayer("3");
  public static final Player<String> PFOUR = makeSimplePlayer("4");
  public static final Player<String> PFIVE = makeSimplePlayer("5");
  public static final Player<String> PINACTIVE1 = makeSimplePlayer("A1", true);
  public static final Player<String> PINACTIVE2 = makeSimplePlayer("A2", true);

  public static Player<String> makeSimplePlayer(final String id, final boolean disabled) {
    StringPlayer player = new StringPlayer();
    player.setId(id);
    player.setSource("MADEUP");
    player.setSourceId("MADEUP" + id);
    player.setDisplayName(id);
    player.setDisabled(disabled);
    player.setImageUrl("http://somewhere.com/image/" + id);
    player.setProfileUrl("http://somewhere.com/profile/" + id);
    return player;
  }

  public static Player<String> makeSimplePlayer(final String id) {
    return GameCoreTestCase.makeSimplePlayer(id, false);
  }

  public static ManualPlayer<String> makeSimpleManualPlayer(final String id, final String password,
      final boolean verified, final boolean disabled, final boolean admin) {
    StringManualPlayer player = new StringManualPlayer();

    player.setId(id);
    player.setSource("MADEUP");
    player.setSourceId("MADEUP" + id);
    player.setDisplayName(id);
    player.setDisabled(disabled);
    player.setAdminUser(admin);
    player.setPassword(password);
    player.setVerified(verified);
    player.setImageUrl("http://somewhere.com/image/" + id);
    player.setProfileUrl("http://somewhere.com/profile/" + id);
    return player;
  }

  public static ManualPlayer<String> makeSimpleManualPlayer(final String id, final String password,
      final boolean verified, final boolean disabled) {
    return GameCoreTestCase.makeSimpleManualPlayer(id, password, verified, disabled, false);
  }

  public static ManualPlayer<String> makeSimpleManualPlayer(final String id, final String password,
      final boolean verified) {
    return GameCoreTestCase.makeSimpleManualPlayer(id, password, verified, false, false);
  }

  public static ManualPlayer<String> makeSimpleManualPlayer(final String id,
      final String password) {
    return GameCoreTestCase.makeSimpleManualPlayer(id, password, true, false, false);
  }

  public static ManualPlayer<String> makeSimpleManualPlayer(final String id) {
    return GameCoreTestCase.makeSimpleManualPlayer(id, "", true, false, false);
  }

  protected static SystemPlayer<String> makeSimpleSystemPlayer(final String id,
      final boolean disabled, final Object admin) {
    StringSystemPlayer player = new StringSystemPlayer();

    player.setId(id);
    player.setSource("MADEUP");
    player.setSourceId("MADEUP" + id);
    player.setDisplayName(id);
    player.setDisabled(disabled);
    player.setAdminUser((boolean) admin);
    player.setImageUrl("http://somewhere.com/image/" + id);
    player.setProfileUrl("http://somewhere.com/profile/" + id);
    return player;
  }

  protected static SystemPlayer<String> makeSimpleSystemPlayer(final String id,
      final boolean disabled) {
    return GameCoreTestCase.makeSimpleSystemPlayer(id, disabled, false);
  }

  protected static SystemPlayer<String> makeSimpleSystemPlayer(final String id) {
    return GameCoreTestCase.makeSimpleSystemPlayer(id, false, false);
  }

  protected static StringGame makeSimpleGame(final String id) {
    StringGame game = new StringGame();
    game.setId(id);
    return game;
  }

  public static StringMPGame makeSimpleMPGame(final String id) {
    StringMPGame game = new StringMPGame();
    game.setId(id);
    return game;
  }

  protected static StringSPGame makeSimpleSPGame(final String id) {
    StringSPGame game = new StringSPGame();
    game.setId(id);
    return game;
  }

  public static String reverse(final String input) {
    return new StringBuilder(input).reverse().toString();
  }
}
