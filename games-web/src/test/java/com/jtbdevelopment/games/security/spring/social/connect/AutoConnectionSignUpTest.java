package com.jtbdevelopment.games.security.spring.social.connect;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.PlayerFactory;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 1/7/15 Time: 7:04 AM
 */
public class AutoConnectionSignUpTest {

  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private PlayerFactory playerFactory = Mockito.mock(PlayerFactory.class);
  private AutoConnectionSignUp autoConnectionSignUp = new AutoConnectionSignUp(playerRepository,
      playerFactory);

  @Test
  public void testFindsPlayerInRepository() {
    String pid = "APLAYER";
    String source = "FBTLI";
    String sourceId = "XXAAMM";
    StringPlayer player = GameCoreTestCase.makeSimplePlayer(pid);
    when(playerRepository.findBySourceAndSourceId(source, sourceId)).thenReturn(player);
    ConnectionKey key = new ConnectionKey(source, sourceId);
    Connection connection = Mockito.mock(Connection.class);
    when(connection.getKey()).thenReturn(key);
    assertEquals(pid, autoConnectionSignUp.execute(connection));
  }

  @Test
  public void testSuccessfullyCreatesPlayer() {
    String pid = "APLAYER";
    String source = "FBTLI";
    String sourceId = "XXAAMM";
    String imageUrl = "http://image";
    String profileUrl = "http://profile";
    String displayName = "displayName";
    StringPlayer player = GameCoreTestCase.makeSimplePlayer(pid + "UNSAVED");
    ReflectionTestUtils.setField(player, "source", "");// have to force unset
    StringPlayer saved = GameCoreTestCase.makeSimplePlayer(pid);
    when(playerRepository.findBySourceAndSourceId(source, sourceId)).thenReturn(null);
    when(playerRepository.save(player)).thenReturn(saved);

    ConnectionKey key = new ConnectionKey(source, sourceId);
    Connection connection = Mockito.mock(Connection.class);
    when(connection.getKey()).thenReturn(key);
    when(connection.getProfileUrl()).thenReturn(profileUrl);
    when(connection.getImageUrl()).thenReturn(imageUrl);
    when(connection.fetchUserProfile())
        .thenReturn(new UserProfileBuilder().setName(displayName).build());
    when(playerFactory.newPlayer()).thenReturn(player);
    assertEquals(pid, autoConnectionSignUp.execute(connection));
    assertEquals(displayName, player.getDisplayName());
    assertEquals(profileUrl, player.getProfileUrl());
    assertEquals(imageUrl, player.getImageUrl());
    assertEquals(sourceId, player.getSourceId());
    assertEquals(source, player.getSource());
    Assert.assertFalse(player.isDisabled());
  }

  @Test
  public void testDAOFailsToCreatesPlayerSilently() {
    String pid = "APLAYER";
    String source = "FBTLI";
    String sourceId = "XXAAMM";
    String imageUrl = "http://image";
    String profileUrl = "http://profile";
    String displayName = "displayName";
    StringPlayer player = GameCoreTestCase.makeSimplePlayer(pid + "UNSAVED");
    when(playerRepository.findBySourceAndSourceId(source, sourceId)).thenReturn(null);
    when(playerRepository.save(player)).thenReturn(null);

    ConnectionKey key = new ConnectionKey(source, sourceId);
    Connection connection = Mockito.mock(Connection.class);
    when(connection.getKey()).thenReturn(key);
    when(connection.getProfileUrl()).thenReturn(profileUrl);
    when(connection.getImageUrl()).thenReturn(imageUrl);
    when(connection.fetchUserProfile())
        .thenReturn(new UserProfileBuilder().setName(displayName).build());
    when(playerFactory.newPlayer()).thenReturn(player);
    assertEquals(null, autoConnectionSignUp.execute(connection));
  }

  @Test
  public void testDAOFailsToCreatesPlayerViaException() {
    String pid = "APLAYER";
    String source = "FBTLI";
    String sourceId = "XXAAMM";
    String imageUrl = "http://image";
    String profileUrl = "http://profile";
    String displayName = "displayName";
    StringPlayer player = GameCoreTestCase.makeSimplePlayer(pid + "UNSAVED");
    when(playerRepository.findBySourceAndSourceId(source, sourceId)).thenReturn(null);
    when(playerRepository.save(player)).thenThrow(new RuntimeException("X"));

    ConnectionKey key = new ConnectionKey(source, sourceId);
    Connection connection = Mockito.mock(Connection.class);
    when(connection.getKey()).thenReturn(key);
    when(connection.getProfileUrl()).thenReturn(profileUrl);
    when(connection.getImageUrl()).thenReturn(imageUrl);
    when(connection.fetchUserProfile())
        .thenReturn(new UserProfileBuilder().setName(displayName).build());
    when(playerFactory.newPlayer()).thenReturn(player);
    assertEquals(null, autoConnectionSignUp.execute(connection));
  }
}
