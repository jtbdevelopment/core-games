package com.jtbdevelopment.games

import com.jtbdevelopment.games.players.AbstractPlayer
import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.Player

/**
 * Date: 11/8/14
 * Time: 9:09 AM
 */
abstract class GameCoreTestCase extends GroovyTestCase {
    protected static final Player<String> PONE = makeSimplePlayer("1")
    protected static final Player<String> PTWO = makeSimplePlayer("2")
    protected static final Player<String> PTHREE = makeSimplePlayer("3")
    protected static final Player<String> PFOUR = makeSimplePlayer("4")
    protected static final Player<String> PFIVE = makeSimplePlayer("5")
    protected static final Player<String> PINACTIVE1 = makeSimplePlayer("A1", true)
    protected static final Player<String> PINACTIVE2 = makeSimplePlayer("A2", true)

    public static class StringPlayer extends AbstractPlayer<String> {
        private String md5
        String id

        void setId(final String id) {
            this.id = id
            computeMD5Hex()
        }

        @Override
        protected void setMd5(final String md5) {
            this.md5 = md5;
        }

        @Override
        protected String getMd5Internal() {
            return this.md5
        }

        @Override
        String getIdAsString() {
            return id
        }
    }

    public static class StringManualPlayer extends StringPlayer implements ManualPlayer<String> {
        String password
        String verificationToken
        boolean verified
    }

    protected static Player<String> makeSimplePlayer(final String id, final boolean disabled = false) {
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

}
