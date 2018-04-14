package com.jtbdevelopment.games.dao.caching;

import com.jtbdevelopment.games.state.Game;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Date: 2/27/15 Time: 9:46 PM
 */
@Component
public class GameKeyUtility {

  public static List<Serializable> collectGameIDs(final Iterable<Game> games) {
    if (games == null) {
      return Collections.emptyList();
    }
    List<Serializable> ids = new LinkedList<>();
    for (Game game : games) {
      ids.add(game.getId());
    }
    return ids;
  }

}
