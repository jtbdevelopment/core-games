package com.jtbdevelopment.games.rest.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Date: 11/13/14 Time: 7:02 AM
 */
@Provider
public class OptimisticLockingFailureExceptionMapper implements
    ExceptionMapper<OptimisticLockingFailureException> {

  private static final Logger logger = LoggerFactory
      .getLogger(OptimisticLockingFailureExceptionMapper.class);

  @Override
  public Response toResponse(final OptimisticLockingFailureException e) {
    logger.info("Mapping error " + e.getMessage());
    return Response.status(Status.CONFLICT)
        .entity(e.getMessage())
        .type(MediaType.TEXT_PLAIN_TYPE)
        .build();
  }
}
