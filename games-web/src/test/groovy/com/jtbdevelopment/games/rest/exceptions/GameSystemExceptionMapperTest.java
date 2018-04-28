package com.jtbdevelopment.games.rest.exceptions;

import com.jtbdevelopment.games.exceptions.GameSystemException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 11/13/14 Time: 7:09 AM
 */
public class GameSystemExceptionMapperTest {

  private GameSystemExceptionMapper mapper = new GameSystemExceptionMapper();

  @Test
  public void testToResponse() {
    final String s = "A MESSAGE";
    Response response = mapper.toResponse(new GameSystemException(s) {
    });
    Assert.assertEquals(s, response.getEntity());
    Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    Assert.assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getMediaType());
  }
}
