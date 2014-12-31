package com.jtbdevelopment.games.rest.exceptions

import com.jtbdevelopment.games.exceptions.GameSystemException
import groovy.transform.CompileStatic

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

/**
 * Date: 11/13/14
 * Time: 6:54 AM
 */
@Provider
@CompileStatic
class GameSystemExceptionMapper implements ExceptionMapper<GameSystemException> {

    @Override
    Response toResponse(final GameSystemException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(e.message)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build()
    }
}
