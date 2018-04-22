package com.jtbdevelopment.games.websocket

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Date: 12/9/14
 * Time: 11:47 AM
 */
class WebSocketJSONConverterTest extends GroovyTestCase {
    WebSocketJSONConverter webSocketJsonConverter = new WebSocketJSONConverter()

    void testEncodeCallsStaticMapper() {
        String expectedString = 'tada i am what the mapper returned'
        WebSocketMessage input = new WebSocketMessage()
        webSocketJsonConverter.objectMapper = [
                writeValueAsString: {
                    Object o ->
                        assert o.is(input)
                        expectedString
                }
        ] as ObjectMapper
        assert expectedString == webSocketJsonConverter.encode(input)
    }

    void testDecodeCallsStaticMapper() {
        String input = 'tada i am what the mapper received'
        WebSocketMessage expectObject = new WebSocketMessage()
        webSocketJsonConverter.objectMapper = [
                readValue: {
                    Object o, Class c ->
                        assert o.is(input)
                        assert c.is(WebSocketMessage.class)
                        expectObject
                }
        ] as ObjectMapper
        assert webSocketJsonConverter.decode(input).is(expectObject)
    }

}