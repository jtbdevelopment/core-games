package com.jtbdevelopment.games.rest.aop

import com.jtbdevelopment.games.rest.exceptions.OptimisticLockingFailureExceptionMapper
import org.springframework.dao.OptimisticLockingFailureException

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Date: 11/13/14
 * Time: 7:05 AM
 */
class OptimisticLockingFailureExceptionMapperTest extends GroovyTestCase {
    OptimisticLockingFailureExceptionMapper mapper = new OptimisticLockingFailureExceptionMapper()

    void testToResponse() {
        String s = "A MESSAGE"
        Response response = mapper.toResponse(new OptimisticLockingFailureException(s))
        assert response.entity == s
        assert response.status == Response.Status.CONFLICT.statusCode
        assert response.mediaType == MediaType.TEXT_PLAIN_TYPE
    }
}
