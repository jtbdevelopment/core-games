package com.jtbdevelopment.games.rest.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 11/13/14 Time: 7:09 AM
 */
public class GameInputExceptionMapperTest {

  private GameInputExceptionMapper mapper = new GameInputExceptionMapper();

  @Test
  public void testToResponse() {
    final String s = "A MESSAGE";
    Response response = mapper.toResponse(new GameInputException(s) {
    });
    Assert.assertEquals(s, response.getEntity());
    Assert.assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
    Assert.assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getMediaType());
  }
}
