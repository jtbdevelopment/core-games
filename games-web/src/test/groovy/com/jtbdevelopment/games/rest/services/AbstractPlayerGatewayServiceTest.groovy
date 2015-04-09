package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.security.SessionUserInfo
import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.TypeChecked
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 11/15/2014
 * Time: 11:28 AM
 */
class AbstractPlayerGatewayServiceTest extends GameCoreTestCase {
    AbstractPlayerGatewayService playerGatewayService = new AbstractPlayerGatewayService() {}

    void testClassAnnotations() {
        assert AbstractPlayerGatewayService.class.getAnnotation(RolesAllowed.class).value() == [PlayerRoles.PLAYER]
        //assert AbstractPlayerGatewayService.class.getAnnotation(Path.class).value() == "/"
    }

    void testPing() {
        assert AbstractPlayerGatewayService.PING_RESULT == playerGatewayService.ping()
    }

    void testPingAnnotations() {
        def ping = AbstractPlayerGatewayService.getMethod("ping", [] as Class[])
        assert (ping.annotations.size() == 3 ||
                (ping.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && ping.annotations.size() == 4)
        )
        assert ping.isAnnotationPresent(GET.class)
        assert ping.isAnnotationPresent(Produces.class)
        assert ping.getAnnotation(Produces.class).value() == [MediaType.TEXT_PLAIN]
        assert ping.isAnnotationPresent(Path.class)
        assert ping.getAnnotation(Path.class).value() == "ping"
    }

    void testValidPlayer() {
        SecurityContextHolder.context = new SecurityContextImpl()
        SecurityContextHolder.context.authentication = new TestingAuthenticationToken(new SessionUserInfo<String>() {
            @Override
            Player<String> getSessionUser() {
                return null
            }

            @Override
            Player<String> getEffectiveUser() {
                return PONE
            }

            @Override
            void setEffectiveUser(final Player<String> player) {

            }
        }, null)
        AbstractPlayerServices services = [playerID: new ThreadLocal<String>()] as AbstractPlayerServices
        playerGatewayService.playerServices = services

        assert services.is(playerGatewayService.gameServices())
        assert services.playerID.get() == PONE.id
    }

    void testGameServicesAnnotations() {
        def gameServices = AbstractPlayerGatewayService.getMethod("gameServices", [] as Class[])
        assert (gameServices.annotations.size() == 1 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 2)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "player"
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

    void testGetPhases() {
        playerGatewayService.phasesAndDescriptions() == [
                (GamePhase.Challenged)      : GamePhase.Challenged.description,
                (GamePhase.Declined)        : GamePhase.Declined.description,
                (GamePhase.NextRoundStarted): GamePhase.NextRoundStarted.description,
                (GamePhase.Playing)         : GamePhase.Playing.description,
                (GamePhase.Quit)            : GamePhase.Quit.description,
                (GamePhase.Setup)           : GamePhase.Setup.description,
                (GamePhase.RoundOver)       : GamePhase.RoundOver.description,
        ]
    }

    void testGetPhasesAnnotations() {
        def gameServices = AbstractPlayerGatewayService.getMethod("phasesAndDescriptions", [] as Class[])
        assert (gameServices.annotations.size() == 3 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 4)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "phases"
        assert gameServices.isAnnotationPresent(GET.class)
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }
}

