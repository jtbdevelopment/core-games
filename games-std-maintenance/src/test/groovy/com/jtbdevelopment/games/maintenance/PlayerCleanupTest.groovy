package com.jtbdevelopment.games.maintenance

import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository
import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringManualPlayer
import com.jtbdevelopment.games.StringSystemPlayer
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import org.springframework.social.connect.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 8/18/15
 * Time: 10:50 PM
 */
class PlayerCleanupTest extends GameCoreTestCase {
    PlayerCleanup playerCleanup = new PlayerCleanup()

    StringSystemPlayer system = makeSimpleSystemPlayer("system 1")
    StringManualPlayer manual = makeSimpleManualPlayer("manual 1")

    void testDeleteOlderPlayersWithoutSocialConnection() {
        Instant start = ZonedDateTime.now(ZoneId.of('GMT')).minusDays(90).toInstant()
        def deleted = [] as Set
        playerCleanup.playerRepository = [
                findByLastLoginLessThan: {
                    Instant cutoff ->
                        assert start <= cutoff
                        assert start.plusSeconds(60) > cutoff
                        return [PONE, system, manual]
                },
                delete                 : {
                    deleted.add(it)
                }
        ] as AbstractPlayerRepository

        playerCleanup.deleteInactivePlayers()
        assert [PONE] as Set == deleted
    }

    void testDeleteOlderPLayersWithSomeSocialConnections() {
        Instant start = ZonedDateTime.now(ZoneId.of('GMT')).minusDays(90).toInstant()
        def deleted = [] as Set
        playerCleanup.playerRepository = [
                findByLastLoginLessThan: {
                    Instant cutoff ->
                        assert start <= cutoff
                        assert start.plusSeconds(60) > cutoff
                        return [PONE, PTWO, manual, system, PTHREE]
                },
                delete                 : {
                    deleted.add(it)
                }
        ] as AbstractPlayerRepository

        def poneKey1 = new ConnectionKey("a", "1")
        def poneKey2 = new ConnectionKey("b", "2")
        def poneKey3 = new ConnectionKey("a4x", "23xfr")
        def ptwoKey1 = new ConnectionKey("g", "3")
        def removedConnections = [] as Set
        def poneRepo = [
                findAllConnections: {
                    MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>()
                    connections.put("fb", new LinkedList<Connection<?>>())
                    connections.put("tweet", new LinkedList<Connection<?>>())
                    connections.add("fb", new LocalConnection(key: poneKey1))
                    connections.add("fb", new LocalConnection(key: poneKey2))
                    connections.add("tweet", new LocalConnection(key: poneKey3))
                    connections
                },
                removeConnection  : {
                    ConnectionKey k ->
                        removedConnections.add(k)
                }

        ] as ConnectionRepository
        def ptwoRepo = [
                findAllConnections: {
                    MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>()
                    connections.put("fb", new LinkedList<Connection<?>>())
                    connections.put("tweet", new LinkedList<Connection<?>>())
                    connections.add("tweet", new LocalConnection(key: ptwoKey1))
                    connections
                },
                removeConnection  : {
                    ConnectionKey k ->
                        removedConnections.add(k)
                }
        ] as ConnectionRepository

        AbstractUsersConnectionRepository connectionRepository = mock(AbstractUsersConnectionRepository.class)
        when(connectionRepository.createConnectionRepository(PONE.id)).thenReturn(poneRepo)
        when(connectionRepository.createConnectionRepository(PTWO.id)).thenReturn(ptwoRepo)
        when(connectionRepository.createConnectionRepository(PTHREE.id)).thenReturn(null)
        playerCleanup.usersConnectionRepository = connectionRepository
        playerCleanup.deleteInactivePlayers()

        assert [PONE, PTWO, PTHREE] as Set == deleted
        assert removedConnections == [poneKey1, poneKey2, poneKey3, ptwoKey1] as Set
    }

    //  Only thing we need for this is getKey
    private class LocalConnection implements Connection {
        ConnectionKey key

        String getDisplayName() {
            return null
        }

        String getProfileUrl() {
            return null
        }

        String getImageUrl() {
            return null
        }

        void sync() {

        }

        boolean test() {
            return false
        }

        boolean hasExpired() {
            return false
        }

        void refresh() {

        }

        UserProfile fetchUserProfile() {
            return null
        }

        void updateStatus(final String message) {

        }

        Object getApi() {
            return null
        }

        ConnectionData createData() {
            return null
        }
    }
}
