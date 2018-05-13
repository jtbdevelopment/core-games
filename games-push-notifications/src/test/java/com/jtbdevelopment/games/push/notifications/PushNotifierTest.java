package com.jtbdevelopment.games.push.notifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

/**
 * Date: 10/18/2015 Time: 9:05 PM
 */
public class PushNotifierTest {

  private ObjectMapper objectMapper = mock(ObjectMapper.class);
  private AbstractPlayerRepository playerRepository = mock(AbstractPlayerRepository.class);
  private Builder builder = mock(Builder.class);
  private Client client = mock(Client.class);
  private PushNotifier notifier = new PushNotifier(objectMapper, playerRepository, client, builder);
  private Map<String, Object> baseMessage = notifier.createBaseMessage();

  @Test
  public void testSimpleCase() {
    StringPlayer player = (StringPlayer) GameCoreTestCase.makeSimplePlayer("31");
    RegisteredDevice device1 = new RegisteredDevice();
    device1.setDeviceID("dev1");
    RegisteredDevice device2 = new RegisteredDevice();
    device2.setDeviceID("dev2");
    player.updateRegisteredDevice(device1);
    player.updateRegisteredDevice(device2);
    LinkedHashMap<String, Integer> map = new LinkedHashMap<>(2);
    map.put("canonical_ids", 0);
    map.put("failure", 0);
    LinkedHashMap<String, Integer> response = map;
    HashMap expectedMessage = new HashMap(baseMessage);
    expectedMessage.put("registration_ids", new ArrayList<>(Arrays.asList("dev1", "dev2")));
    Entity<HashMap> entity = Entity.entity(expectedMessage, MediaType.APPLICATION_JSON_TYPE);
    GenericType<Map<String, Object>> type = new GenericType<Map<String, Object>>() {
    };
    when(builder.post(Matchers.eq(entity),
        (GenericType) Matchers.eq((GenericType<Map<String, Object>>) type))).thenReturn(response);
    assertTrue(notifier.notifyPlayer(player, null));
  }

  @Test
  public void testCaseWhereWeReceiveVaryingResponsesOnStatus() {
    StringPlayer player = (StringPlayer) GameCoreTestCase.makeSimplePlayer("31");
    RegisteredDevice device1 = new RegisteredDevice();
    device1.setDeviceID("good1");
    RegisteredDevice device2 = new RegisteredDevice();
    device2.setDeviceID("old1");
    RegisteredDevice device3 = new RegisteredDevice();
    device3.setDeviceID("unavailable");
    RegisteredDevice device4 = new RegisteredDevice();
    device4.setDeviceID("notreg");
    RegisteredDevice device5 = new RegisteredDevice();
    device5.setDeviceID("invalid");
    player.updateRegisteredDevice(device1);
    player.updateRegisteredDevice(device2);
    player.updateRegisteredDevice(device3);
    player.updateRegisteredDevice(device4);
    player.updateRegisteredDevice(device5);
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("canonical_ids", 1);
    map.put("failure", 3);
    Map<String, String> map1 = new HashMap<>();
    map1.put("message_id", "123");
    Map<String, String> map2 = new HashMap<>();
    map2.put("registration_id", "newid");
    map2.put("message_id", "xa");
    Map<String, String> map3 = new HashMap<>();
    map3.put("error", "Unavailable");
    Map<String, String> map4 = new HashMap<>();
    map4.put("error", "NotRegistered");
    Map<String, String> map5 = new HashMap<>();
    map5.put("error", "InvalidRegistration");
    //  this and
    map.put("results", Arrays.asList(map3, map4, map5, map1, map2));
    Map<String, Object> response = map;
    HashMap expectedMessage = new HashMap(baseMessage);
    expectedMessage.put("registration_ids",
        // this need to be in same order
        Arrays.asList("unavailable", "notreg", "invalid", "good1", "old1"));
    Entity<HashMap> entity = Entity.entity(expectedMessage, MediaType.APPLICATION_JSON_TYPE);
    GenericType<Map<String, Object>> type = new GenericType<Map<String, Object>>() {
    };
    when(builder.post(Matchers.eq(entity),
        (GenericType) Matchers.eq((GenericType<Map<String, Object>>) type))).thenReturn(response);
    assertTrue(notifier.notifyPlayer(player, null));
    ArgumentCaptor<StringPlayer> captor = ArgumentCaptor.forClass(StringPlayer.class);
    verify(playerRepository).save(captor.capture());
    StringPlayer saved = captor.getValue();
    List<String> ids = saved.getRegisteredDevices().stream().map(d -> d.getDeviceID())
        .collect(Collectors.toList());
    assertEquals(3, ids.size());
    assertTrue(ids.contains("good1"));
    assertTrue(ids.contains("newid"));
    assertTrue(ids.contains("unavailable"));

  }
}
