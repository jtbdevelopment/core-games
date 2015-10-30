package com.jtbdevelopment.games.push.notifications

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.notifications.RegisteredDevice
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
 *
 * From Google API Docs - the type of response you will receive{"multicast_id": 216,
 "success": 3,
 "failure": 3,
 "canonical_ids": 1,
 "results": [{ "message_id": "1:0408" },{ "error": "Unavailable" },{ "error": "InvalidRegistration" },{ "message_id": "1:1516" },{ "message_id": "1:2342", "registration_id": "32" },{ "error": "NotRegistered"}]}*
 */
@Component
@CompileStatic
class PushNotifier {
    private static final Logger logger = LoggerFactory.getLogger(PushNotifier.class)

    private static final String GCM_URL = "https://gcm-http.googleapis.com/gcm/send"
    private static final URI GCM_URI = UriBuilder.fromUri(GCM_URL).build()
    private static int DEFAULT_TTL = 60 * 60 * 4 // 4 hours in seconds

    protected Client client = ClientBuilder.newClient()
    protected Builder builder
    protected Map<String, Object> baseMessage

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    PushProperties pushProperties

    @Autowired
    AbstractPlayerRepository playerRepository

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

        baseMessage = new HashMap<String, Object>([
                //  common
                collapse_key     : "YourTurn",
                time_to_live     : DEFAULT_TTL,

                //  ios
                content_available: true,
                notification     : [
                        //  common
                        title  : "Your turn.",
                        body   : "Your turn to play.",

                        //  Android
                        icon   : "icon",
                        tag    : "YourTurn",
                        message: "Your turn to play.",

                        // ios
                        badge  : "1",

                ]
        ])
    }

    boolean notifyPlayer(final Player player, final MultiPlayerGame game) {
        try {
            Map<String, Object> message = new HashMap<>(baseMessage)
            //  TODO - allow app greater control of message sent

            //  TODO - create user device groups
            List<String> deviceIDs = player.registeredDevices.collect { it.deviceID }
            message["registration_ids"] = deviceIDs
            Entity entity = Entity.entity(message, MediaType.APPLICATION_JSON)
            logger.trace("Posting to GCM message " + objectMapper?.writeValueAsString(message));
            Map<String, Object> result = builder.post(entity, new GenericType<Map<String, Object>>() {})
            logger.trace("GCM posted with result " + result)
            if (result["failure"] != 0 || result["canonical_ids"] != 0) {
                boolean playerUpdated = false
                List<Map<String, String>> results = (List<Map<String, String>>) result["results"]
                for (int i = 0; i < results.size(); ++i) {
                    def subResult = results[i]
                    String error = subResult["error"]
                    if ("InvalidRegistration" == error || "NotRegistered" == error) {
                        player.removeRegisteredDevice(player.registeredDevices.find { it.deviceID == deviceIDs[i] })
                        playerUpdated = true
                    }
                    String newRegId = subResult["registration_id"]
                    if (newRegId) {
                        player.removeRegisteredDevice(player.registeredDevices.find { it.deviceID == deviceIDs[i] })
                        player.updateRegisteredDevice(new RegisteredDevice(deviceID: newRegId))
                        playerUpdated = true
                    }
                }
                if (playerUpdated) {
                    playerRepository.save(player)
                }
            }
            return true
        } catch (Exception ex) {
            logger.error("Error publishing to GCM ", ex)
            return false
        }
    }
}
