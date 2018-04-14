package com.jtbdevelopment.games.state

import com.jtbdevelopment.games.players.Player
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version

import java.lang.reflect.Field

/**
 * Date: 1/7/15
 * Time: 6:40 AM
 */
class AbstractGameTest extends GroovyTestCase {
    private static class StringGame extends AbstractGame<String, Object> {
        String id
        String previousId

        @Override
        String getIdAsString() {
            return id
        }

        @Override
        String getPreviousIdAsString() {
            return id
        }

        @Override
        List<Player<String>> getAllPlayers() {
            null
        }
    }

    private static class DerivedStringGame extends StringGame {
        String anotherField
    }

    private static class ComplexStringIdGame extends StringGame {
        @Override
        String getIdAsString() {
            return super.getIdAsString() + 'XYZ'
        }
    }

    void testVersionAnnotations() {
        Field m = AbstractGame.class.getDeclaredField('version')
        assert m
        assert m.getAnnotation(Version.class)
    }

    void testCreatedAnnotations() {
        Field m = AbstractGame.class.getDeclaredField('created')
        assert m
        assert m.getAnnotation(CreatedDate.class)
    }

    void testLastUpdateTimestampAnnotations() {
        Field m = AbstractGame.class.getDeclaredField('lastUpdate')
        assert m
        assert m.getAnnotation(LastModifiedDate.class)
    }

    void testEquals() {
        StringGame game = new StringGame(id: 'TEST')
        assert game == new StringGame(id: game.id)
        assert game == new DerivedStringGame(id: game.id, anotherField: 'X')
        assert game == new ComplexStringIdGame(id: game.id)
        assertFalse game == new StringGame(id: game.id.toLowerCase())
        assertFalse game == game.id
        assertFalse game == null
    }

    void testHashCodeNullId() {
        assert new StringGame().hashCode() == 0
    }

    void testHashCodeUsesIdAsString() {
        ComplexStringIdGame game = new ComplexStringIdGame(id: 'TEST')
        assert game.hashCode() == 'TESTXYZ'.hashCode()
    }

    void testConstructor() {
        StringGame game = new StringGame()
        assertNull game.id
        assertNull game.lastUpdate
        assertNull game.created
        assertNull game.completedTimestamp
        assertTrue game.featureData.isEmpty()
        assertTrue game.features.isEmpty()
        assert 0 == game.round
        assertNull game.previousId
        assert GamePhase.Setup == game.gamePhase
    }
}
