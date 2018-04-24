package com.jtbdevelopment.games.datagrid.hazelcast.cluster;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage.ClusterMessageType;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.stringimpl.StringToStringConverter;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 3/4/15 Time: 7:36 AM
 */
public class UpdatesFromClusterListenerTest {

  private HazelcastInstance hazelcastInstance = Mockito.mock(HazelcastInstance.class);
  private ITopic topic = Mockito.mock(ITopic.class);
  private Message<ClusterMessage> message = Mockito.mock(Message.class);
  private Member member = Mockito.mock(Member.class);
  private PlayerPublisher playerPublisher = Mockito.mock(PlayerPublisher.class);
  private GamePublisher gamePublisher = Mockito.mock(GamePublisher.class);
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private AbstractGameRepository gameRepository = Mockito.mock(AbstractGameRepository.class);
  private UpdatesFromClusterListener listener;

  @Before
  public void setup() {
    Mockito.when(hazelcastInstance.getTopic(UpdatesToClusterPublisher.PUB_SUB_TOPIC))
        .thenReturn(topic);
    listener = new UpdatesFromClusterListener(hazelcastInstance, gamePublisher, playerPublisher,
        new StringToStringConverter(), gameRepository, playerRepository);
    Mockito.when(message.getPublishingMember()).thenReturn(member);
  }

  @Test
  public void testSetup() {
    Assert.assertSame(topic, listener.topic);
  }

  @Test
  public void testNullMessage() {
    listener.onMessage(null);
    //  dont explode
  }

  @Test
  public void testNullObject() {
    Mockito.when(message.getMessageObject()).thenReturn(null);
    Mockito.when(member.localMember()).thenReturn(false);
    listener.onMessage(message);
    //  dont explode
  }

  @Test
  public void testPublishAllPlayersFromOther() {
    ClusterMessage clusterMessage = new ClusterMessage();
    clusterMessage.setClusterMessageType(ClusterMessageType.AllPlayersUpdate);
    Mockito.when(message.getMessageObject()).thenReturn(clusterMessage);
    Mockito.when(member.localMember()).thenReturn(false);
    listener.onMessage(message);
    Mockito.verify(playerPublisher).publishAll(false);
  }

  @Test
  public void testPublishAllPlayersFromLocal() {
    ClusterMessage clusterMessage = new ClusterMessage();
    clusterMessage.setClusterMessageType(ClusterMessageType.AllPlayersUpdate);
    Mockito.when(message.getMessageObject()).thenReturn(clusterMessage);
    Mockito.when(member.localMember()).thenReturn(true);
    listener.onMessage(message);
    Mockito.verify(playerPublisher, Mockito.never()).publishAll(false);
  }

  @Test
  public void testPublishPlayer() {
    ClusterMessage clusterMessage = new ClusterMessage();
    clusterMessage.setClusterMessageType(ClusterMessageType.PlayerUpdate);
    clusterMessage.setPlayerId(GameCoreTestCase.reverse(PONE.getIdAsString()));
    Mockito.when(message.getMessageObject()).thenReturn(clusterMessage);
    Mockito.when(member.localMember()).thenReturn(false);
    Mockito.when(playerRepository.findById(PONE.getIdAsString())).thenReturn(Optional.of(PONE));
    listener.onMessage(message);
    Mockito.verify(playerPublisher).publish(PONE, false);
  }

  @Test
  public void testPublishPlayerFromLocal() {
    ClusterMessage clusterMessage = new ClusterMessage();
    clusterMessage.setClusterMessageType(ClusterMessageType.PlayerUpdate);
    clusterMessage.setPlayerId(GameCoreTestCase.reverse(PONE.getIdAsString()));
    Mockito.when(message.getMessageObject()).thenReturn(clusterMessage);
    Mockito.when(member.localMember()).thenReturn(true);
    Mockito.when(playerRepository.findById(PONE.getIdAsString())).thenReturn(Optional.of(PONE));
    listener.onMessage(message);
    Mockito.verify(playerPublisher, Mockito.never()).publish(PONE, false);
  }

  @Test
  public void testPublishGame() {
    ClusterMessage clusterMessage = new ClusterMessage();
    clusterMessage.setClusterMessageType(ClusterMessageType.GameUpdate);
    clusterMessage.setPlayerId(GameCoreTestCase.reverse(PONE.getIdAsString()));
    MultiPlayerGame game = Mockito.mock(MultiPlayerGame.class);
    String gameId = "3434";
    clusterMessage.setGameId(GameCoreTestCase.reverse(gameId));
    Mockito.when(message.getMessageObject()).thenReturn(clusterMessage);
    Mockito.when(member.localMember()).thenReturn(false);
    Mockito.when(playerRepository.findById(PONE.getIdAsString())).thenReturn(Optional.of(PONE));
    Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    listener.onMessage(message);
    Mockito.verify(gamePublisher).publish(game, PONE, false);
  }

  @Test
  public void testPublishGameFromLocal() {
    ClusterMessage clusterMessage = new ClusterMessage();
    clusterMessage.setClusterMessageType(ClusterMessageType.GameUpdate);
    clusterMessage.setPlayerId(GameCoreTestCase.reverse(PONE.getIdAsString()));
    MultiPlayerGame game = Mockito.mock(MultiPlayerGame.class);
    String gameId = "3434";
    clusterMessage.setGameId(GameCoreTestCase.reverse(gameId));
    Mockito.when(message.getMessageObject()).thenReturn(clusterMessage);
    Mockito.when(member.localMember()).thenReturn(true);
    Mockito.when(playerRepository.findById(PONE.getIdAsString())).thenReturn(Optional.of(PONE));
    Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    listener.onMessage(message);
    Mockito.verify(gamePublisher, Mockito.never()).publish(game, PONE, false);
  }
}
