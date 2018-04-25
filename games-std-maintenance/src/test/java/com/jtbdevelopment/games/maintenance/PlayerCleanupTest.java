package com.jtbdevelopment.games.maintenance;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository;
import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.stringimpl.StringManualPlayer;
import com.jtbdevelopment.games.stringimpl.StringSystemPlayer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Date: 8/18/15 Time: 10:50 PM
 */
public class PlayerCleanupTest {

  private StringSystemPlayer system = GameCoreTestCase.makeSimpleSystemPlayer("system 1");
  private StringManualPlayer manual = GameCoreTestCase.makeSimpleManualPlayer("manual 1");

  @Test
  public void testDeleteOlderPlayersWithoutSocialConnection() {
    final Instant start = ZonedDateTime.now(ZoneId.of("GMT")).minusDays(90).toInstant();
    AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
    when(playerRepository.findByLastLoginLessThan(Matchers.isA(Instant.class)))
        .then(invocation -> {
          Instant cutoff = (Instant) invocation.getArguments()[0];
          TestCase.assertTrue(start.compareTo(cutoff) <= 0);
          TestCase.assertTrue(start.plusSeconds(60).compareTo(cutoff) > 0);
          return new ArrayList<>(Arrays.asList(PONE, system, manual));
        });
    PlayerCleanup playerCleanup = new PlayerCleanup(playerRepository, null);
    playerCleanup.deleteInactivePlayers();
    verify(playerRepository).delete(PONE);
    verify(playerRepository, Mockito.never()).delete(system);
    verify(playerRepository, Mockito.never()).delete(manual);
  }

  @Test
  public void testDeleteOlderPLayersWithSomeSocialConnections() {
    final Instant start = ZonedDateTime.now(ZoneId.of("GMT")).minusDays(90).toInstant();
    AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
    when(playerRepository.findByLastLoginLessThan(Matchers.isA(Instant.class)))
        .then(invocation -> {
          Instant cutoff = (Instant) invocation.getArguments()[0];
          TestCase.assertTrue(start.compareTo(cutoff) <= 0);
          TestCase.assertTrue(start.plusSeconds(60).compareTo(cutoff) > 0);
          return new ArrayList<>(
              Arrays.asList(PONE, PTWO, manual, system, PTHREE));
        });
    ConnectionRepository poneRepo = Mockito.mock(ConnectionRepository.class);
    ConnectionRepository ptwoRepo = Mockito.mock(ConnectionRepository.class);

    ConnectionKey poneKey1 = new ConnectionKey("a", "1");
    ConnectionKey poneKey2 = new ConnectionKey("b", "2");
    ConnectionKey poneKey3 = new ConnectionKey("a4x", "23xfr");
    ConnectionKey ptwoKey1 = new ConnectionKey("g", "3");
    MultiValueMap<String, Connection<?>> poneConnections = new LinkedMultiValueMap<>();
    poneConnections
        .put("fb", new LinkedList<>());
    poneConnections
        .put("tweet", new LinkedList<>());
    poneConnections
        .add("fb", makeConnection(poneKey1));
    poneConnections
        .add("fb", makeConnection(poneKey2));
    poneConnections
        .add("tweet", makeConnection(poneKey3));
    when(poneRepo.findAllConnections()).thenReturn(poneConnections);
    MultiValueMap<String, Connection<?>> ptwoConnections = new LinkedMultiValueMap<>();
    ptwoConnections
        .put("fb", new LinkedList<>());
    ptwoConnections
        .put("tweet", new LinkedList<>());
    ptwoConnections
        .add("tweet", makeConnection(ptwoKey1));
    when(ptwoRepo.findAllConnections()).thenReturn(ptwoConnections);
    AbstractUsersConnectionRepository connectionRepository = Mockito
        .mock(AbstractUsersConnectionRepository.class);
    when(connectionRepository.createConnectionRepository(PONE.getId()))
        .thenReturn(poneRepo);
    when(connectionRepository.createConnectionRepository(PTWO.getId()))
        .thenReturn(ptwoRepo);
    when(connectionRepository.createConnectionRepository(PTHREE.getId())).thenReturn(null);
    PlayerCleanup playerCleanup = new PlayerCleanup(playerRepository, connectionRepository);
    playerCleanup.deleteInactivePlayers();

    verify(playerRepository).delete(PONE);
    verify(playerRepository).delete(PTWO);
    verify(playerRepository).delete(PTHREE);
    verify(playerRepository, Mockito.never()).delete(system);
    verify(playerRepository, Mockito.never()).delete(manual);
    verify(poneRepo).removeConnection(poneKey1);
    verify(poneRepo).removeConnection(poneKey2);
    verify(poneRepo).removeConnection(poneKey3);
    verify(ptwoRepo).removeConnection(ptwoKey1);
  }

  private Connection makeConnection(ConnectionKey connectionKey) {
    Connection connection = Mockito.mock(Connection.class);
    when(connection.getKey()).thenReturn(connectionKey);
    return connection;
  }
}
