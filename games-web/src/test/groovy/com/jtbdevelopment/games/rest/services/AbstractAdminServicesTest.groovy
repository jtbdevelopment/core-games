package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.security.SessionUserInfo
import groovy.transform.TypeChecked
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

import javax.annotation.security.RolesAllowed
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.lang.reflect.Method
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 12/24/14
 * Time: 2:23 PM
 */
class AbstractAdminServicesTest extends GameCoreTestCase {
    AbstractAdminServices adminServices = new AbstractAdminServices() {}

    void testClassAnnotations() {
        assert AbstractAdminServices.class.isAnnotationPresent(RolesAllowed.class)
        assert AbstractAdminServices.class.getAnnotation(RolesAllowed.class).value() == [PlayerRoles.ADMIN]
    }

    void testPlayerCount() {
        long expectedCount = 5
        adminServices.playerRepository = [
                count: {
                    return expectedCount
                }
        ] as AbstractPlayerRepository

        assert expectedCount == adminServices.players()
    }

    void testGetPlayersAnnotations() {
        Method m = AbstractAdminServices.class.getMethod("players", [] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(GET.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.TEXT_PLAIN]
        assert m.isAnnotationPresent(Path.class)
        assert m.getAnnotation(Path.class).value() == "playerCount"
    }

    void testGameCount() {
        long expectedCount = 15
        adminServices.gameRepository = [
                count: {
                    return expectedCount
                }
        ] as AbstractGameRepository

        assert expectedCount == adminServices.games()
    }

    void testGetGamesAnnotations() {
        Method m = AbstractAdminServices.class.getMethod("games", [] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(GET.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.TEXT_PLAIN]
        assert m.isAnnotationPresent(Path.class)
        assert m.getAnnotation(Path.class).value() == "gameCount"
    }

    void testPlayerCreatedSinceCount() {
        long expectedCount = 5
        def now = ZonedDateTime.now(ZoneId.of("GMT"))
        ZonedDateTime since = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(now.toEpochSecond()),
                ZoneId.of("GMT"))
        adminServices.playerRepository = [
                countByCreatedGreaterThan: {
                    z ->
                        assert since.equals(z)
                        return expectedCount
                }
        ] as AbstractPlayerRepository

        assert expectedCount == adminServices.playersCreatedSince(since.toEpochSecond())
    }

    void testGetPlayersCreatedSinceAnnotations() {
        Method m = AbstractAdminServices.class.getMethod("playersCreatedSince", [long.class] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(GET.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.TEXT_PLAIN]
        def params = m.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "since"
        assert m.isAnnotationPresent(Path.class)
        assert m.getAnnotation(Path.class).value() == "playersCreated/{since}"
    }

    void testPlayerLastLoginSinceCount() {
        long expectedCount = 5
        def now = ZonedDateTime.now(ZoneId.of("GMT"))
        ZonedDateTime since = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(now.toEpochSecond()),
                ZoneId.of("GMT"))
        adminServices.playerRepository = [
                countByLastLoginGreaterThan: {
                    z ->
                        assert since.equals(z)
                        return expectedCount
                }
        ] as AbstractPlayerRepository

        assert expectedCount == adminServices.playersLoggedInSince(since.toEpochSecond())
    }

    void testGetPlayersLastLoginSinceAnnotations() {
        Method m = AbstractAdminServices.class.getMethod("playersLoggedInSince", [long.class] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(GET.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.TEXT_PLAIN]
        def params = m.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "since"
        assert m.isAnnotationPresent(Path.class)
        assert m.getAnnotation(Path.class).value() == "playersLoggedIn/{since}"
    }

    void testGamesSinceCount() {
        long expectedCount = 5

        def now = ZonedDateTime.now(ZoneId.of("GMT"))
        ZonedDateTime since = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(now.toEpochSecond()),
                ZoneId.of("GMT"))
        adminServices.gameRepository = [
                countByCreatedGreaterThan: {
                    z ->
                        assert since.equals(z)
                        expectedCount
                }
        ] as AbstractGameRepository

        assert expectedCount == adminServices.gamesSince(since.toEpochSecond())
    }

    void testGamesSinceAnnotations() {
        Method m = AbstractAdminServices.class.getMethod("gamesSince", [long.class] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(GET.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.TEXT_PLAIN]
        def params = m.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "since"
        assert m.getAnnotation(Path.class).value() == "gamesSince/{since}"
    }

    void testPlayersToSimulateLikeNoPageParams() {
        def repoResult = new PageImpl<Player>([PTWO, PTHREE])
        def likeString = 'Hey Joe'
        def repo = [
                findByDisplayNameContains: {
                    String like, PageRequest pageRequest ->
                        assert likeString == like
                        assert pageRequest.pageNumber == AbstractAdminServices.DEFAULT_PAGE
                        assert pageRequest.pageSize == AbstractAdminServices.DEFAULT_PAGE_SIZE
                        assert pageRequest.sort.properties.size() == 1
                        assert pageRequest.sort.getOrderFor("displayName").direction == Sort.Direction.ASC
                        repoResult
                }
        ] as AbstractPlayerRepository
        adminServices.playerRepository = repo;

        assert adminServices.playersToSimulateLike(likeString, null, null).is(repoResult)
    }

    void testPlayersToSimulateLikeAnnotations() {
        Method m = AbstractAdminServices.class.getMethod("playersToSimulateLike", [String.class, Integer.class, Integer.class] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(GET.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert m.isAnnotationPresent(Path.class)
        assert "playersLike" == m.getAnnotation(Path.class).value()
        def params = m.parameterAnnotations
        assert params.length == 3
        assert params[0].length == 1
        assert params[0][0].annotationType() == QueryParam.class
        assert ((QueryParam) params[0][0]).value() == "like"
        assert params[1].length == 1
        assert params[1][0].annotationType() == QueryParam.class
        assert ((QueryParam) params[1][0]).value() == "page"
        assert params[2].length == 1
        assert params[2][0].annotationType() == QueryParam.class
        assert ((QueryParam) params[2][0]).value() == "pageSize"
    }

    void testSwitchEffectiveUser() {
        adminServices.stringToIDConverter = new GameCoreTestCase.StringToStringConverter()
        SecurityContextHolder.context = new SecurityContextImpl()
        def session = new SessionUserInfo<String>() {
            Player<String> sessionUser = PONE
            Player<String> effectiveUser = PONE

            @Override
            Player<String> getSessionUser() {
                return sessionUser
            }

            @Override
            Player<String> getEffectiveUser() {
                return effectiveUser
            }

            @Override
            void setEffectiveUser(final Player<String> player) {
                this.effectiveUser = player
            }
        }
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(session, null)
        SecurityContextHolder.context.authentication = authenticationToken

        def repo = [
                findOne: {
                    String id ->
                        assert id == PTWO.id
                        return PTWO
                }
        ] as AbstractPlayerRepository

        adminServices.playerRepository = repo

        assert PTWO.is(adminServices.switchEffectiveUser(PTWO.idAsString))
        assert session.effectiveUser == PTWO
        assert session.sessionUser == PONE
    }

    void testSwitchEffectiveUserBadID() {
        adminServices.stringToIDConverter = new GameCoreTestCase.StringToStringConverter()
        SecurityContextHolder.context = new SecurityContextImpl()
        def session = new SessionUserInfo<String>() {
            Player<String> sessionUser = PONE
            Player<String> effectiveUser = PONE

            @Override
            Player<String> getSessionUser() {
                return sessionUser
            }

            @Override
            Player<String> getEffectiveUser() {
                return effectiveUser
            }

            @Override
            void setEffectiveUser(final Player<String> player) {
                this.effectiveUser = player
            }
        }
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(session, null)
        SecurityContextHolder.context.authentication = authenticationToken

        def repo = [
                findOne: {
                    String id ->
                        assert id == PTWO.id
                        return null
                }
        ] as AbstractPlayerRepository

        adminServices.playerRepository = repo


        def response = adminServices.switchEffectiveUser(PTWO.idAsString)
        assert response in Response
        assert response.status == Response.Status.NOT_FOUND.statusCode
        assert response.mediaType == MediaType.TEXT_PLAIN_TYPE
        assert session.effectiveUser == PONE
        assert session.sessionUser == PONE
    }

    void testSwitchAnnotations() {
        Method m = AbstractAdminServices.class.getMethod("switchEffectiveUser", [String.class] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(PUT.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert m.isAnnotationPresent(Path.class)
        assert m.getAnnotation(Path.class).value() == "{playerID}"
        def params = m.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "playerID"
    }

}
