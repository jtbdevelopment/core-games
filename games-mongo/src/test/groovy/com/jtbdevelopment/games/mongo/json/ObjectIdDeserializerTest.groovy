package com.jtbdevelopment.games.mongo.json

import com.fasterxml.jackson.core.*
import org.bson.types.ObjectId

/**
 * Date: 12/22/14
 * Time: 12:06 PM
 */
class ObjectIdDeserializerTest extends GroovyTestCase {
    ObjectIdDeserializer objectIdDeserializer = new ObjectIdDeserializer()

    private class FakeParser extends JsonParser {
        ObjectId id;

        public FakeParser(final ObjectId id) {
            this.id = id;
        }

        @Override
        ObjectCodec getCodec() {
            return null
        }

        @Override
        void setCodec(final ObjectCodec c) {

        }

        @Override
        Version version() {
            return null
        }

        @Override
        void close() throws IOException {

        }

        @Override
        JsonToken nextToken() throws IOException, JsonParseException {
            return null
        }

        @Override
        JsonToken nextValue() throws IOException, JsonParseException {
            return null
        }

        @Override
        JsonParser skipChildren() throws IOException, JsonParseException {
            return null
        }

        @Override
        boolean isClosed() {
            return false
        }

        @Override
        JsonToken getCurrentToken() {
            return null
        }

        @Override
        int getCurrentTokenId() {
            return 0
        }

        @Override
        boolean hasCurrentToken() {
            return false
        }

        @Override
        boolean hasTokenId(final int id) {
            return false
        }

        @Override
        String getCurrentName() throws IOException, JsonParseException {
            return null
        }

        @Override
        JsonStreamContext getParsingContext() {
            return null
        }

        @Override
        JsonLocation getTokenLocation() {
            return null
        }

        @Override
        JsonLocation getCurrentLocation() {
            return null
        }

        @Override
        void clearCurrentToken() {

        }

        @Override
        JsonToken getLastClearedToken() {
            return null
        }

        @Override
        void overrideCurrentName(final String name) {

        }

        @Override
        String getText() throws IOException, JsonParseException {
            return null
        }

        @Override
        char[] getTextCharacters() throws IOException, JsonParseException {
            return new char[0]
        }

        @Override
        int getTextLength() throws IOException, JsonParseException {
            return 0
        }

        @Override
        int getTextOffset() throws IOException, JsonParseException {
            return 0
        }

        @Override
        boolean hasTextCharacters() {
            return false
        }

        @Override
        Number getNumberValue() throws IOException, JsonParseException {
            return null
        }

        @Override
        JsonParser.NumberType getNumberType() throws IOException, JsonParseException {
            return null
        }

        @Override
        int getIntValue() throws IOException, JsonParseException {
            return 0
        }

        @Override
        long getLongValue() throws IOException, JsonParseException {
            return 0
        }

        @Override
        BigInteger getBigIntegerValue() throws IOException, JsonParseException {
            return null
        }

        @Override
        float getFloatValue() throws IOException, JsonParseException {
            return 0
        }

        @Override
        double getDoubleValue() throws IOException, JsonParseException {
            return 0
        }

        @Override
        BigDecimal getDecimalValue() throws IOException, JsonParseException {
            return null
        }

        @Override
        Object getEmbeddedObject() throws IOException, JsonParseException {
            return null
        }

        @Override
        byte[] getBinaryValue(final Base64Variant b64variant) throws IOException, JsonParseException {
            return new byte[0]
        }

        @Override
        String getValueAsString(final String defaultValue) throws IOException, JsonParseException {
            return id.toHexString()
        }
    }

    void testDeserialize() {
        ObjectId start = new ObjectId()
        def jparse = new FakeParser(start)

        assert objectIdDeserializer.deserialize(jparse, null) == start
    }

    void testGetRegisterForClass() {
        assert ObjectId.class.is(objectIdDeserializer.registerForClass())
    }
}

