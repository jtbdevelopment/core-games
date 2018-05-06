package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.games.state.GamePhase;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:02 PM
 */
@Component
public class StringToGamePhaseConverter implements Converter<String, GamePhase> {

  @Override
  public GamePhase convert(final String source) {
    //noinspection ConstantConditions
    return source != null ? GamePhase.valueOf(source) : null;
  }

}
