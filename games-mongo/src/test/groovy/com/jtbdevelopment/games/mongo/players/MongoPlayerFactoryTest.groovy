package com.jtbdevelopment.games.mongo.players

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributesFactory
import com.jtbdevelopment.games.players.Player
import org.springframework.data.annotation.Transient

/**
 * Date: 1/8/15
 * Time: 10:10 PM
 */
class MongoPlayerFactoryTest extends GroovyTestCase {
    MongoPlayerFactory factory = new MongoPlayerFactory()

    private static class GameAttributes implements GameSpecificPlayerAttributes {
        int magicValue
        @Transient
        Player player
    }

    private static class GameAttributesFactory implements GameSpecificPlayerAttributesFactory {

        @Override
        GameSpecificPlayerAttributes newPlayerAttributes() {
            return new GameAttributes(magicValue: 1)
        }

        @Override
        GameSpecificPlayerAttributes newManualPlayerAttributes() {
            return new GameAttributes(magicValue: 2)
        }

        @Override
        GameSpecificPlayerAttributes newSystemPlayerAttributes() {
            return new GameAttributes(magicValue: 3)
        }
    }

    void testNewPlayer() {
        def player = factory.newPlayer()
        assert player instanceof MongoPlayer
        assertNull player.gameSpecificPlayerAttributes
    }

    void testNewManualPlayer() {
        def player = factory.newManualPlayer()
        assert player instanceof MongoManualPlayer
        assertNull player.gameSpecificPlayerAttributes
    }

    void testNewSystemPlayer() {
        def player = factory.newSystemPlayer()
        assert player instanceof MongoSystemPlayer
        assertNull player.gameSpecificPlayerAttributes
    }

    void testNewPlayerWithGameAttributes() {
        factory.gameSpecificPlayerAttributesFactory = new GameAttributesFactory()
        def player = factory.newPlayer()
        assert player instanceof MongoPlayer
        assertNotNull player.gameSpecificPlayerAttributes
        assert player.is(player.gameSpecificPlayerAttributes.player)
        assert ((GameAttributes) player.gameSpecificPlayerAttributes).magicValue == 1
    }

    void testNewManualPlayerWithGameAttributes() {
        factory.gameSpecificPlayerAttributesFactory = new GameAttributesFactory()
        def player = factory.newManualPlayer()
        assert player instanceof MongoManualPlayer
        assertNotNull player.gameSpecificPlayerAttributes
        assert player.is(player.gameSpecificPlayerAttributes.player)
        assert ((GameAttributes) player.gameSpecificPlayerAttributes).magicValue == 2
    }

    void testNewSystemPlayerWithGameAttributes() {
        factory.gameSpecificPlayerAttributesFactory = new GameAttributesFactory()
        def player = factory.newSystemPlayer()
        assert player instanceof MongoSystemPlayer
        assertNotNull player.gameSpecificPlayerAttributes
        assert player.is(player.gameSpecificPlayerAttributes.player)
        assert ((GameAttributes) player.gameSpecificPlayerAttributes).magicValue == 3
    }
}
