package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.security.SessionUserInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder

import javax.annotation.security.RolesAllowed
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Date: 11/27/2014
 * Time: 6:34 PM
 *
 * Abstract to allow additional changes
 */
@CompileStatic
@RolesAllowed([PlayerRoles.ADMIN])
abstract class AbstractAdminServices {
    public static final int DEFAULT_PAGE = 0
    public static final int DEFAULT_PAGE_SIZE = 500
    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    StringToIDConverter<? extends Serializable> stringToIDConverter

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Object playersToSimulate(
            @QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize) {
        return playerRepository.findAll(new PageRequest(
                page ?: DEFAULT_PAGE,
                pageSize ?: DEFAULT_PAGE_SIZE,
                Sort.Direction.ASC,
                'displayName')).toList() as Set
    }

    @PUT
    @Path("{playerID}")
    @Produces(MediaType.APPLICATION_JSON)
    Object switchEffectiveUser(@PathParam("playerID") final String effectivePlayerID) {
        Player p = playerRepository.findOne(stringToIDConverter.convert(effectivePlayerID));
        if (p != null) {
            ((SessionUserInfo) SecurityContextHolder.context.authentication.principal).effectiveUser = p;
            return p;
        }
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build()
    }
}
