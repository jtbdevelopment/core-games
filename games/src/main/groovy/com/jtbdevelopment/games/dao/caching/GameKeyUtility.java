package com.jtbdevelopment.games.dao.caching;

import com.jtbdevelopment.games.state.Game;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
    return StreamSupport.stream(games.spliterator(), false)
        .map(Game::getId)
        .collect(Collectors.toList());
  }

}
