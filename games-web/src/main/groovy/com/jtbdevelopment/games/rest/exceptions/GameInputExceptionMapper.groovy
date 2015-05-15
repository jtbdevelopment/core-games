package com.jtbdevelopment.games.rest.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

/**
 * Date: 11/13/14
 * Time: 7:02 AM
 */
@Provider
@CompileStatic
class GameInputExceptionMapper implements ExceptionMapper<GameInputException> {
    private static final Logger logger = LoggerFactory.getLogger(GameInputException.class)

    @Override
    Response toResponse(final GameInputException e) {
        logger.info("Mapping error " + e.message)
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.message)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build()
    }
}
