package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.jtbdevelopment.games.state.GamePhase;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:01 PM
 */
@Component
@WritingConverter
public class GamePhaseToStringConverter implements MongoConverter<GamePhase, String> {

  @Override
  public String convert(final GamePhase source) {
    //noinspection ConstantConditions
    return source != null ? source.toString() : null;
  }

}
