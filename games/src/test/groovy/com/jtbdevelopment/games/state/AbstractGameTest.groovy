package com.jtbdevelopment.games.state

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
        assert game.equals(new StringGame(id: game.id))
        assert game.equals(new DerivedStringGame(id: game.id, anotherField: 'X'))
        assert game.equals(new ComplexStringIdGame(id: game.id))
        assertFalse game.equals(new StringGame(id: game.id.toLowerCase()))
        assertFalse game.equals(game.id)
        assertFalse game.equals(null)
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
        assert game.id == null
        assert game.lastUpdate == null
        assert game.created == null
        assert game.completedTimestamp == null
        assert game.featureData.isEmpty()
        assert game.features.isEmpty()
        assert 0 == game.round
        assertNull game.previousId
        assertNull game.gamePhase
    }
}
