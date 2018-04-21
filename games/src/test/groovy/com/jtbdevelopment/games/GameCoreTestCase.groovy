package com.jtbdevelopment.games

import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.SystemPlayer

/**
 * Date: 11/8/14
 * Time: 9:09 AM
 */
abstract class GameCoreTestCase extends GroovyTestCase {
    public static final Player<String> PONE = makeSimplePlayer("1")
    public static final Player<String> PTWO = makeSimplePlayer("2")
    public static final Player<String> PTHREE = makeSimplePlayer("3")
    public static final Player<String> PFOUR = makeSimplePlayer("4")
    public static final Player<String> PFIVE = makeSimplePlayer("5")
    public static final Player<String> PINACTIVE1 = makeSimplePlayer("A1", true)
    public static final Player<String> PINACTIVE2 = makeSimplePlayer("A2", true)

    static class StringToStringConverter implements StringToIDConverter<String> {
        @Override
        String convert(final String source) {
            return source?.reverse()
        }
    }

    protected static Player<String> makeSimplePlayer(
            final String id, final boolean disabled = false) {
        return new StringPlayer(
                id: id,
                source: "MADEUP",
                sourceId: "MADEUP" + id,
                displayName: id,
                disabled: disabled,
                imageUrl: "http://somewhere.com/image/" + id,
                profileUrl: "http://somewhere.com/profile/" + id)
    }

    protected static ManualPlayer<String> makeSimpleManualPlayer(
            final String id,
            final String password = "",
            final boolean verified = true, final boolean disabled = false, final admin = false) {
        return new StringManualPlayer(
                id: id,
                source: "MADEUP",
                sourceId: "MADEUP" + id,
                displayName: id,
                disabled: disabled,
                adminUser: admin,
                password: password,
                verified: verified,
                imageUrl: "http://somewhere.com/image/" + id,
                profileUrl: "http://somewhere.com/profile/" + id)
    }

    protected static SystemPlayer<String> makeSimpleSystemPlayer(
            final String id, final boolean disabled = false, final admin = false) {
        return new StringSystemPlayer(
                id: id,
                source: "MADEUP",
                sourceId: "MADEUP" + id,
                displayName: id,
                disabled: disabled,
                adminUser: admin,
                imageUrl: "http://somewhere.com/image/" + id,
                profileUrl: "http://somewhere.com/profile/" + id)
    }

    protected static StringGame makeSimpleGame(final String id) {
        return new StringGame(id: id)
    }

    protected static StringMPGame makeSimpleMPGame(final String id) {
        return new StringMPGame(id: id)
    }

    protected static StringSPGame makeSimpleSPGame(final String id) {
        return new StringSPGame(id: id)
    }
}
