package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.dao.AbstractGameRepository
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
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

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
    AbstractGameRepository gameRepository
    public static final ZoneId GMT = ZoneId.of("GMT")

    @Autowired
    StringToIDConverter<? extends Serializable> stringToIDConverter

    @GET
    @Path("gamesSince/{since}")
    @Produces(MediaType.TEXT_PLAIN)
    long gamesSince(@PathParam("since") long since) {
        return gameRepository.countByCreatedGreaterThan(ZonedDateTime.ofInstant(Instant.ofEpochSecond(since), GMT))
    }

    @GET
    @Path("playerCount")
    @Produces(MediaType.TEXT_PLAIN)
    long players() {
        return playerRepository.count()
    }

    @GET
    @Path("gameCount")
    @Produces(MediaType.TEXT_PLAIN)
    long games() {
        return gameRepository.count()
    }

    @GET
    @Path("playersCreated/{since}")
    @Produces(MediaType.TEXT_PLAIN)
    long playersCreatedSince(@PathParam("since") long since) {
        return playerRepository.countByCreatedGreaterThan(ZonedDateTime.ofInstant(Instant.ofEpochSecond(since), GMT))
    }

    @GET
    @Path("playersLoggedIn/{since}")
    @Produces(MediaType.TEXT_PLAIN)
    long playersLoggedInSince(@PathParam("since") long since) {
        return playerRepository.countByLastLoginGreaterThan(ZonedDateTime.ofInstant(Instant.ofEpochSecond(since), GMT))
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("playersLike")
    Object playersToSimulateLike(
            @QueryParam("like") String like,
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize) {
        return playerRepository.findByDisplayNameContains(
                like,
                new PageRequest(
                        (int) (page ?: DEFAULT_PAGE),
                        (int) (pageSize ?: DEFAULT_PAGE_SIZE),
                        Sort.Direction.ASC,
                        'displayName'))
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
