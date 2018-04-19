package com.jtbdevelopment.games.rest.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 11/13/14 Time: 7:02 AM
 */
@Provider
public class GameInputExceptionMapper implements ExceptionMapper<GameInputException> {

  private static final Logger logger = LoggerFactory.getLogger(GameInputException.class);

  @Override
  public Response toResponse(final GameInputException e) {
    logger.info("Mapping error " + e.getMessage());
    return Response.status(Status.CONFLICT).entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE)
        .build();
  }
}
