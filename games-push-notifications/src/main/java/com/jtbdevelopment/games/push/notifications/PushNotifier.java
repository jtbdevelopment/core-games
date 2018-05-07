package com.jtbdevelopment.games.push.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import com.jtbdevelopment.games.push.PushProperties;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 10/11/2015 Time: 8:34 PM
 *
 * From Google API Docs - the type of response you will receive{"multicast_id": 216, "success": 3,
 * "failure": 3, "canonical_ids": 1, "results": [{ "message_id": "1:0408" },{ "error": "Unavailable"
 * },{ "error": "InvalidRegistration" },{ "message_id": "1:1516" },{ "message_id": "1:2342",
 * "registration_id": "32" },{ "error": "NotRegistered"}]}*
 */
@Component
public class PushNotifier<ID extends Serializable, P extends AbstractPlayer<ID>> {

  private static final Logger logger = LoggerFactory.getLogger(PushNotifier.class);
  private static final String GCM_URL = "https://gcm-http.googleapis.com/gcm/send";
  private static final URI GCM_URI = UriBuilder.fromUri(GCM_URL).build();
  @SuppressWarnings("FieldCanBeLocal")
  private static int DEFAULT_TTL = 60 * 60 * 4;
  private final Builder builder;
  private final Map<String, Object> baseMessage;
  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final ObjectMapper objectMapper;
  private final Client client;

  @Autowired
  public PushNotifier(
      final ObjectMapper objectMapper,
      final PushProperties pushProperties,
      final AbstractPlayerRepository<ID, P> playerRepository) {
    this.playerRepository = playerRepository;
    this.objectMapper = objectMapper;
    client = ClientBuilder.newClient();
    client.register(
        new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
    builder = client.target(GCM_URI).request(MediaType.APPLICATION_JSON_TYPE)
        .header("Content-Type", MediaType.APPLICATION_JSON)
        .header("Authorization", "key=" + pushProperties.getApiKey());
    baseMessage = createBaseMessage();
  }

  //  for testing
  @SuppressWarnings("WeakerAccess")
  PushNotifier(
      final ObjectMapper objectMapper,
      final AbstractPlayerRepository<ID, P> playerRepository,
      final Client client,
      final Builder builder) {
    this.builder = builder;
    this.playerRepository = playerRepository;
    this.objectMapper = objectMapper;
    this.client = client;
    baseMessage = createBaseMessage();
  }

  Map<String, Object> createBaseMessage() {
    Map<String, Object> message = new HashMap<>();
    message.put("collapse_key", "YourTurn");
    message.put("time_to_live", DEFAULT_TTL);
    message.put("content_available", true);
    Map<String, String> notification = new LinkedHashMap<>();
    notification.put("title", "Your turn.");
    notification.put("body", "Your turn to play.");
    notification.put("icon", "icon");
    notification.put("tag", "YourTurn");
    notification.put("message", "Your turn to play.");
    notification.put("badge", "1");
    message.put("notification", notification);
    return message;
  }

  @SuppressWarnings("WeakerAccess")
  public boolean notifyPlayer(final P player,
      @SuppressWarnings("unused") final AbstractMultiPlayerGame game) {
    try {
      Map<String, Object> message = new HashMap<>(baseMessage);
      //  TODO - allow app greater control of message sent

      //  TODO - create user device groups?
      List<String> deviceIDs = player.getRegisteredDevices()
          .stream()
          .map(RegisteredDevice::getDeviceID)
          .collect(Collectors.toList());
      message.put("registration_ids", deviceIDs);

      Entity entity = Entity.entity(message, MediaType.APPLICATION_JSON);
      if (objectMapper != null) {
        logger.trace("Posting to GCM message " + objectMapper.writeValueAsString(message));
      }
      Map<String, Object> result = builder.post(entity, new GenericType<Map<String, Object>>() {
      });
      logger.trace("GCM posted with result " + result);
      if (!result.get("failure").equals(0) || !result.get("canonical_ids").equals(0)) {
        Set<String> devicesToRemove = new HashSet<>();
        //noinspection unchecked
        List<Map<String, String>> results = (List<Map<String, String>>) result.get("results");
        for (int i = 0; i < results.size(); ++i) {
          Map<String, String> subResult = results.get(i);
          String error = subResult.get("error");
          if ("InvalidRegistration".equals(error) || "NotRegistered".equals(error)) {
            devicesToRemove.add(deviceIDs.get(i));
          }
          String newRegId = subResult.get("registration_id");
          if (StringUtils.hasText(newRegId)) {
            devicesToRemove.add(deviceIDs.get(i));
            RegisteredDevice registeredDevice = new RegisteredDevice();
            registeredDevice.setDeviceID(newRegId);
            player.updateRegisteredDevice(registeredDevice);
          }
        }
        player.setRegisteredDevices(
            player.getRegisteredDevices()
                .stream()
                .filter(x -> !devicesToRemove.contains(x.getDeviceID()))
                .collect(Collectors.toSet()));
        playerRepository.save(player);

      }
    } catch (Exception ex) {
      logger.error("Error publishing to GCM ", ex);
      return false;
    }
    return true;
  }
}
