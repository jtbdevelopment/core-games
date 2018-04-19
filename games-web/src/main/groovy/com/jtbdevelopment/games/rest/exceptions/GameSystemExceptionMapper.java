package com.jtbdevelopment.games.rest.exceptions;

import com.jtbdevelopment.games.exceptions.GameSystemException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 11/13/14 Time: 6:54 AM
 */
@Provider
public class GameSystemExceptionMapper implements ExceptionMapper<GameSystemException> {

  private static final Logger logger = LoggerFactory.getLogger(GameSystemExceptionMapper.class);

  @Override
  public Response toResponse(final GameSystemException e) {
    logger.info("Mapping error " + e.getMessage());
    return Response.status(Status.NOT_FOUND).entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE)
        .build();
  }
}
