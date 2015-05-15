package com.jtbdevelopment.games.rest.exceptions

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException

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
class OptimisticLockingFailureExceptionMapper implements ExceptionMapper<OptimisticLockingFailureException> {
    private static final Logger logger = LoggerFactory.getLogger(OptimisticLockingFailureExceptionMapper.class)

    @Override
    Response toResponse(final OptimisticLockingFailureException e) {
        logger.info("Mapping error " + e.message)
        return Response.status(Response.Status.CONFLICT)
                .entity(e.message)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build()
    }
}
