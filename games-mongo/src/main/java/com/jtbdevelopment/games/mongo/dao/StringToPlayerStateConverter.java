package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.jtbdevelopment.games.state.PlayerState;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:02 PM
 */
@Component
@ReadingConverter
public class StringToPlayerStateConverter implements MongoConverter<String, PlayerState> {

  @Override
  public PlayerState convert(final String source) {
    //noinspection ConstantConditions
    return source != null ? PlayerState.valueOf(source) : null;
  }

}
