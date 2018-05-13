package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.games.state.GamePhase;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:01 PM
 */
@Component
public class GamePhaseToStringConverter implements Converter<GamePhase, String> {

  @Override
  public String convert(final GamePhase source) {
    //noinspection ConstantConditions
    return source != null ? source.toString() : null;
  }

}
