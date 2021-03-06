package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.jtbdevelopment.games.state.GamePhase;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:02 PM
 */
@Component
@ReadingConverter
public class StringToGamePhaseConverter implements MongoConverter<String, GamePhase> {

  @Override
  public GamePhase convert(final String source) {
    //noinspection ConstantConditions
    return source != null ? GamePhase.valueOf(source) : null;
  }

}
