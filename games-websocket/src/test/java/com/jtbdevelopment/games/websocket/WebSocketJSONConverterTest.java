package com.jtbdevelopment.games.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 12/9/14 Time: 11:47 AM
 */
public class WebSocketJSONConverterTest {

  private ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
  private WebSocketJSONConverter webSocketJsonConverter = new WebSocketJSONConverter();

  @Before
  public void setup() {
    webSocketJsonConverter.setMapper(mapper);
  }

  @Test
  public void testEncodeCallsStaticMapper() throws JsonProcessingException {
    String expectedString = "tada i am what the mapper returned";
    WebSocketMessage input = new WebSocketMessage();
    Mockito.when(mapper.writeValueAsString(input)).thenReturn(expectedString);
    Assert.assertEquals(expectedString, webSocketJsonConverter.encode(input));
  }

  @Test
  public void testDecodeCallsStaticMapper() throws IOException {
    String input = "tada i am what the mapper received";
    WebSocketMessage expectObject = new WebSocketMessage();
    Mockito.when(mapper.readValue(input, WebSocketMessage.class)).thenReturn(expectObject);
    Assert.assertSame(expectObject, webSocketJsonConverter.decode(input));
  }
}
