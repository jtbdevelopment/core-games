package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.players.friendfinder.FriendFinder
import com.jtbdevelopment.games.security.SessionUserInfo
import groovy.transform.TypeChecked
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Date: 11/15/2014
 * Time: 12:02 PM
 */
class AbstractPlayerServicesTest extends GameCoreTestCase {
    AbstractPlayerServices playerServices = new AbstractPlayerServices() {}

    void testValidPlayer() {
        AbstractGameServices services = [playerID: new ThreadLocal<String>(), gameID: new ThreadLocal<String>()] as AbstractGameServices
        playerServices.gamePlayServices = services

        def APLAYER = "PLAYER"
        def AGAME = "GAME"
        playerServices.playerID.set(APLAYER)
        playerServices.stringToIDConverter = new GameCoreTestCase.StringToStringConverter()
        assert services.is(playerServices.gamePlay(AGAME))
        assert services.playerID.get() == APLAYER
        assert services.gameID.get() == AGAME.reverse()
    }

    void testNullGame() {
        playerServices.gamePlayServices = null

        def APLAYER = "APLAYER"
        playerServices.playerID.set(APLAYER)

        OutboundJaxrsResponse resp = playerServices.gamePlay(null)
        assert resp.status == Response.Status.BAD_REQUEST.statusCode
        assert resp.entity == "Missing game identity"
    }

    void testEmptyGame() {
        playerServices.gamePlayServices = null

        def APLAYER = "APLAYER"
        playerServices.playerID.set(APLAYER)

        OutboundJaxrsResponse resp = playerServices.gamePlay("   ")
        assert resp.status == Response.Status.BAD_REQUEST.statusCode
        assert resp.entity == "Missing game identity"
    }


    void testGamePlayAnnotations() {
        def gamePlay = AbstractPlayerServices.getMethod("gamePlay", [String.class] as Class[])
        assert (gamePlay.annotations.size() == 1 ||
                (gamePlay.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gamePlay.annotations.size() == 2)
        )
        assert gamePlay.isAnnotationPresent(Path.class)
        assert gamePlay.getAnnotation(Path.class).value() == "game/{gameID}"
        def params = gamePlay.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "gameID"
    }

    void testPlayerInfo() {
        playerServices.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
                }
        ] as AbstractPlayerRepository

        playerServices.playerID.set(PONE.idAsString)

        def returned = playerServices.playerInfo()
        assert PONE.is(returned)
    }

    void testPlayerInfoAnnotations() {
        def gameServices = AbstractPlayerServices.getMethod("playerInfo", [] as Class[])
        assert (gameServices.annotations.size() == 2 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 3)
        )
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(GET.class)
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testGetFriends() {
        def id = PFOUR.id
        playerServices.playerID.set(id)
        def friendFinder = [
                findFriends: {
                    String it ->
                        assert it == id
                        return ['1': '2', '3': '4', '5': '6']
                }
        ] as FriendFinder
        playerServices.applicationContext = [
                getBean: {
                    Class<?> it ->
                        assert it.is(FriendFinder.class)
                        return friendFinder
                }
        ] as ApplicationContext


        assert playerServices.getFriends() == ['1': '2', '3': '4', '5': '6']
    }

    void testFriendsInfoAnnotations() {
        def gameServices = AbstractPlayerServices.getMethod("getFriends", [] as Class[])
        assert (gameServices.annotations.size() == 3 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 4)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "friends"
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(GET.class)
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testGetFriendsNoAppContext() {
        playerServices.applicationContext = null
        try {
            playerServices.getFriends()
            fail("should fail")
        } catch (IllegalStateException e) {
            //
        }
    }

    void testAdminServicesAnnotation() {
        def gameServices = AbstractPlayerServices.getMethod("adminServices", [] as Class[])
        assert (gameServices.annotations.size() == 2 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 3)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "admin"
        assert gameServices.isAnnotationPresent(RolesAllowed.class)
        assert gameServices.getAnnotation(RolesAllowed.class).value() == [PlayerRoles.ADMIN]
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testAdminServices() {
        def APLAYER = PONE
        def REALPLAYER = PTWO
        def adminServices = [
                playerID: new ThreadLocal<String>()
        ] as AbstractAdminServices

        SecurityContextHolder.context = new SecurityContextImpl()
        SecurityContextHolder.context.authentication = new TestingAuthenticationToken(new SessionUserInfo<String>() {
            @Override
            Player<String> getSessionUser() {
                return REALPLAYER
            }

            @Override
            Player<String> getEffectiveUser() {
                return null
            }

            @Override
            void setEffectiveUser(final Player<String> player) {

            }
        }, null)
        playerServices.adminServices = adminServices
        playerServices.playerID.set(APLAYER)
        assert adminServices.is(playerServices.adminServices())
    }
}
