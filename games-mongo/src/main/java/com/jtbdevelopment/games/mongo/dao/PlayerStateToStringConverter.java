package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.jtbdevelopment.games.state.PlayerState;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:01 PM
 */
@Component
@WritingConverter
public class PlayerStateToStringConverter implements MongoConverter<PlayerState, String> {

  @Override
  public String convert(final PlayerState source) {
    //noinspection ConstantConditions
    return source != null ? source.toString() : null;
  }

}
