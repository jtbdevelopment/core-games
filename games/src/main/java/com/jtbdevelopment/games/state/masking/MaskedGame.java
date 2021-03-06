package com.jtbdevelopment.games.state.masking;

import com.jtbdevelopment.games.state.Game;
import java.util.Map;

/**
 * Date: 2/18/15 Time: 6:54 PM
 */
public interface MaskedGame<FEATURES> extends Game<String, Long, FEATURES> {

  int getRound();

  void setRound(final int round);

  Map<String, String> getPlayers();

  void setPlayers(final Map<String, String> players);

  Map<String, String> getPlayerImages();

  @SuppressWarnings("unused")
  void setPlayerImages(final Map<String, String> playerImages);

  Map<String, String> getPlayerProfiles();

  @SuppressWarnings("unused")
  void setPlayerProfiles(final Map<String, String> players);
}
