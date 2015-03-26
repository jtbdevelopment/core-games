package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.players.friendfinder.FriendFinder
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.StringUtils

import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Date: 11/14/14
 * Time: 6:40 AM
 */
@CompileStatic
abstract class AbstractPlayerServices<ID extends Serializable> implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPlayerServices.class)
    ThreadLocal<ID> playerID = new ThreadLocal<>()

    @Autowired
    AbstractGameServices gamePlayServices
    @Autowired
    AbstractPlayerRepository playerRepository
    @Autowired
    AbstractAdminServices adminServices
    @Autowired
    StringToIDConverter<ID> stringToIDConverter

    private ApplicationContext applicationContext;

    @Path("game/{gameID}")
    Object gamePlay(@PathParam("gameID") final String gameID) {
        if (StringUtils.isEmpty(gameID) || StringUtils.isEmpty(gameID.trim())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing game identity").build()
        }
        gamePlayServices.gameID.set(stringToIDConverter.convert(gameID))
        gamePlayServices.playerID.set(playerID.get())
        return gamePlayServices
    }

    @Override
    void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object playerInfo() {
        return playerRepository.findOne(playerID.get())
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("friends")
    public Map<String, Object> getFriends() {
        //  Social Media Requires Session Specific Requests
        if (applicationContext) {
            logger.info("Able to retrieve FriendFinder from application context");
            FriendFinder friendFinder = applicationContext.getBean(FriendFinder.class)
            return friendFinder?.findFriends(playerID.get())
        } else {
            logger.warn("Unable to retrieve FriendFinder from application context");
            throw new IllegalStateException("No App Context")
        }
    }

    @Path("admin")
    @RolesAllowed([PlayerRoles.ADMIN])
    public Object adminServices() {
        return adminServices
    }
}
