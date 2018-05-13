package com.jtbdevelopment.games.datagrid.hazelcast.cluster;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import com.jtbdevelopment.games.publish.cluster.AbstractUpdatesFromClusterListener;
import com.jtbdevelopment.games.publish.cluster.ClusterMessage;
import com.jtbdevelopment.games.state.AbstractGame;
import java.io.Serializable;
import org.springframework.stereotype.Component;

/**
 * Date: 3/3/15 Time: 7:51 PM
 */
@Component
public class UpdatesFromClusterListener<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractUpdatesFromClusterListener<ID, FEATURES, IMPL, P>
    implements MessageListener<ClusterMessage> {

  final ITopic<ClusterMessage> topic;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  public UpdatesFromClusterListener(
      final HazelcastInstance hazelcastInstance,
      final GamePublisher<IMPL, P> gamePublisher,
      final PlayerPublisher playerPublisher,
      final StringToIDConverter<ID> stringToIDConverter,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository,
      final AbstractPlayerRepository<ID, P> playerRepository) {
    super(gamePublisher, playerPublisher, stringToIDConverter, playerRepository, gameRepository);
    topic = hazelcastInstance.getTopic(UpdatesToClusterPublisher.PUB_SUB_TOPIC);
    topic.addMessageListener(this);
  }

  @Override
  public void onMessage(final Message<ClusterMessage> message) {
    if (message != null && !message.getPublishingMember().localMember()) {
      ClusterMessage clusterMessage = message.getMessageObject();
      if (clusterMessage != null) {
        receiveClusterMessage(clusterMessage);
      }

    }

  }
}
