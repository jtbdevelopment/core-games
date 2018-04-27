package com.jtbdevelopment.games.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.atmosphere.config.managed.Decoder;
import org.atmosphere.config.managed.Encoder;
import org.springframework.stereotype.Component;

/**
 * Date: 12/8/14 Time: 6:52 AM
 */
@Component
public class WebSocketJSONConverter implements Encoder<WebSocketMessage, String>,
    Decoder<String, WebSocketMessage> {

  private final ObjectMapper mapper;

  public WebSocketJSONConverter(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public String encode(final WebSocketMessage input) {
    try {
      return mapper.writeValueAsString(input);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public WebSocketMessage decode(final String s) {
    try {
      return mapper.readValue(s, WebSocketMessage.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
