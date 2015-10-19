package com.jtbdevelopment.games.push.notifications

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.push.PushProperties
import com.jtbdevelopment.games.state.MultiPlayerGame
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation.Builder
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriBuilder

/**
 * Date: 10/11/2015
 * Time: 8:34 PM
 */
@Component
@CompileStatic
class PushNotifier {
    private static final Logger logger = LoggerFactory.getLogger(PushNotifier.class)

    private static final String GCM_URL = "https://gcm-http.googleapis.com/gcm/send"
    private static final URI GCM_URI = UriBuilder.fromUri(GCM_URL).build()
    private static int DEFAULT_TTL = 60 * 60 * 4 // 4 hours in seconds

    //  TODO - allow app greater control of message sent

    protected Client client = ClientBuilder.newClient()
    protected Builder builder
    protected Map<String, Object> baseMessage = [:]

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    PushProperties pushProperties

    @Autowired
    public void setup() {
        client.register(
                new JacksonJaxbJsonProvider(
                        objectMapper,
                        JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS))
        builder = client.
                target(GCM_URI).
                request(MediaType.APPLICATION_JSON_TYPE).
                header("Content-Type", MediaType.APPLICATION_JSON).
                header("Authorization", "key=" + pushProperties.apiKey)
        baseMessage["collapse_key"] = "YourTurn"
        baseMessage["time_to_live"] = DEFAULT_TTL
        baseMessage["notification"] = new HashMap<String, Object>([
                title: "Your turn.",
                body : "Your turn to play.",
                icon : "icon",
                tag  : "YourTurn",
        ])
    }

    boolean notifyPlayer(final Player player, final MultiPlayerGame game) {
        try {
            Map<String, Object> message = new HashMap<>(baseMessage)
            //  TODO - create user device groups
            List<String> deviceIDs = player.registeredDevices.collect { it.deviceID }
            message["registration_ids"] = deviceIDs.join(",")
            Entity entity = Entity.entity(message, MediaType.APPLICATION_JSON)
            logger.trace("Posting to GCM message " + message)
            Map<String, Object> result = builder.post(entity, new GenericType<Map<String, Object>>() {})
            logger.trace("GCM posted with result " + result)
            if (result["failure"] != 0 && result["canonical_ids"] != 0) {
                //  TODO
                /*
                { "multicast_id": 216,
  "success": 3,
  "failure": 3,
  "canonical_ids": 1,
  "results": [
    { "message_id": "1:0408" },
    { "error": "Unavailable" },
    { "error": "InvalidRegistration" },
    { "message_id": "1:1516" },
    { "message_id": "1:2342", "registration_id": "32" },
    { "error": "NotRegistered"}
  ]
}
                 */
            }
            return true
        } catch (Exception ex) {
            logger.error("Error publishing to GCM ", ex)
            return false
        }
    }
}
