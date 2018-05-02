package com.jtbdevelopment.games.rest.aop;

import com.jtbdevelopment.games.rest.exceptions.OptimisticLockingFailureExceptionMapper;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Date: 11/13/14 Time: 7:05 AM
 */
public class OptimisticLockingFailureExceptionMapperTest {

  private OptimisticLockingFailureExceptionMapper mapper = new OptimisticLockingFailureExceptionMapper();

  @Test
  public void testToResponse() {
    String s = "A MESSAGE";
    Response response = mapper.toResponse(new OptimisticLockingFailureException(s));
    Assert.assertEquals(s, response.getEntity());
    Assert.assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
    Assert.assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getMediaType());
  }
}
