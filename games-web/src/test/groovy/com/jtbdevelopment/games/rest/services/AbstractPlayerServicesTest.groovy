package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.players.friendfinder.FriendFinder
import com.jtbdevelopment.games.security.SessionUserInfo
import com.jtbdevelopment.games.stringimpl.StringToStringConverter
import groovy.transform.TypeChecked
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

import javax.annotation.security.RolesAllowed
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static com.jtbdevelopment.games.GameCoreTestCase.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 11/15/2014
 * Time: 12:02 PM
 */
class AbstractPlayerServicesTest extends GroovyTestCase {
    AbstractPlayerServices playerServices = new AbstractPlayerServices() {}

    void testValidPlayer() {
        AbstractGameServices services = [playerID: new ThreadLocal<String>(), gameID: new ThreadLocal<String>()] as AbstractGameServices
        playerServices.gamePlayServices = services

        def APLAYER = "PLAYER"
        def AGAME = "GAME"
        playerServices.playerID.set(APLAYER)
        playerServices.stringToIDConverter = new StringToStringConverter()
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
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
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

    void testUpdateLastVersionInfo() {
        String newNotes = "NewVersion"
        playerServices.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                },
                save    : {
                    Player p ->
                        assert p.is(PONE)
                        assert newNotes == p.lastVersionNotes
                        return PONE
                }
        ] as AbstractPlayerRepository

        playerServices.playerID.set(PONE.idAsString)

        def returned = playerServices.updateLastVersionNotes(newNotes)
        assert PONE.is(returned)
    }

    void testUpdateVersionNotesInfoAnnotations() {
        def gameServices = AbstractPlayerServices.getMethod("updateLastVersionNotes", [String.class] as Class[])
        assert (gameServices.annotations.size() == 3 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 4)
        )
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(POST.class)
        def params = gameServices.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "versionNotes"
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "lastVersionNotes/{versionNotes}"
    }

    void testGetFriendsV2() {
        def id = PFOUR.id
        playerServices.playerID.set(id)
        FriendFinder friendFinder = mock(FriendFinder.class)
        def friends = ['1': ['2'] as Set, '3': ['4', 'X'] as Set, '5': ['6'] as Set]
        when(friendFinder.findFriendsV2(id)).thenReturn(friends)
        playerServices.applicationContext = [
                getBean: {
                    Class<?> it ->
                        assert it.is(FriendFinder.class)
                        return friendFinder
                }
        ] as ApplicationContext


        assert friends == playerServices.getFriendsV2()
    }

    void testFriendsV2InfoAnnotations() {
        def gameServices = AbstractPlayerServices.getMethod("getFriendsV2", [] as Class[])
        assert (gameServices.annotations.size() == 3 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 4)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "friendsV2"
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(GET.class)
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testGetFriendsV2NoAppContext() {
        playerServices.applicationContext = null
        try {
            playerServices.getFriendsV2()
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
