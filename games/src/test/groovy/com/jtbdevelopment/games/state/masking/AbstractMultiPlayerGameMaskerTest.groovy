package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.players.AbstractPlayer
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame
import com.jtbdevelopment.games.state.PlayerState

import java.time.ZonedDateTime

/**
 * Date: 2/19/15
 * Time: 6:30 PM
 */
//  Not using GameCoreTestCase in order to use Integer keys as part of testing
class AbstractMultiPlayerGameMaskerTest extends GroovyTestCase {
    private static enum Features {
        FeatureA,
        FeatureB
    }

    private static class IntPlayer extends AbstractPlayer<Integer> {
        String md5
        Integer id

        @Override
        protected String getMd5Internal() {
            return this.md5
        }

        @Override
        String getIdAsString() {
            return id?.toString()
        }
    }

    private static class IntGame extends AbstractMultiPlayerGame<Integer, Features> {
        Integer id

        @Override
        String getIdAsString() {
            return id?.toString()
        }
    }

    private static class MaskedIntGame extends AbstractMaskedMultiPlayerGame<Features> {
    }

    static class MaskedIntGameMasker extends AbstractMultiPlayerGameMasker<Integer, Features, IntGame, MaskedIntGame> {
        @Override
        protected MaskedIntGame newMaskedGame() {
            return new MaskedIntGame()
        }

        @Override
        Class getIDClass() {
            return Integer.class
        }
    }

    protected static IntPlayer makeSimplePlayer(final Integer id, final boolean disabled = false) {
        return new IntPlayer(
                id: id,
                source: "MADEUP",
                sourceId: "MADEUP" + id,
                displayName: id.toString(),
                disabled: disabled,
                imageUrl: "http://somewhere.com/image/" + id,
                profileUrl: "http://somewhere.com/profile/" + id)
    }

    MaskedIntGameMasker masker = new MaskedIntGameMasker()
    private static IntPlayer PONE = makeSimplePlayer(1)
    private static IntPlayer PTWO = makeSimplePlayer(1)

    public void testMaskingSinglePlayerGame() {
        IntGame game = new IntGame(
                players: [PONE],
                created: ZonedDateTime.now(),
                completedTimestamp: ZonedDateTime.now(),
                declinedTimestamp: ZonedDateTime.now(),
                featureData: [(Features.FeatureA): ""],
                features: [Features.FeatureA, Features.FeatureB] as Set,
                id: 101,
                initiatingPlayer: PONE.id,
                lastUpdate: ZonedDateTime.now(),
                playerStates: [(PONE.id): PlayerState.Accepted],
                version: 10,
        )

        MaskedIntGame maskedGame = masker.maskGameForPlayer(game, PONE)
        checkUnmaskedGameFields(maskedGame, game)

        assert maskedGame.players == [(PONE.md5): PONE.displayName]
        assert maskedGame.playerImages == [(PONE.md5): PONE.imageUrl]
        assert maskedGame.playerProfiles == [(PONE.md5): PONE.profileUrl]
        assert maskedGame.initiatingPlayer == PONE.md5
        assert maskedGame.playerStates == [(PONE.md5): PlayerState.Accepted]
        assert maskedGame.maskedForPlayerID == PONE.idAsString
        assert maskedGame.maskedForPlayerMD5 == PONE.md5
        assert maskedGame.featureData == game.featureData
    }

    public void testMultiplePlayersWithSomePlayerIDFeatureData() {
        IntGame game = new IntGame(
                players: [PONE, PTWO],
                created: ZonedDateTime.now(),
                completedTimestamp: ZonedDateTime.now(),
                declinedTimestamp: ZonedDateTime.now(),
                featureData: [(Features.FeatureA): "", (Features.FeatureB): PTWO.id],
                features: [Features.FeatureA, Features.FeatureB] as Set,
                id: 105,
                initiatingPlayer: PTWO.id,
                lastUpdate: ZonedDateTime.now(),
                playerStates: [(PONE.id): PlayerState.Accepted, (PTWO.id): PlayerState.Rejected],
                version: 10,
        )

        MaskedIntGame maskedGame = masker.maskGameForPlayer(game, PONE)
        checkUnmaskedGameFields(maskedGame, game)

        assert maskedGame.players == [(PONE.md5): PONE.displayName, (PTWO.md5): PTWO.displayName]
        assert maskedGame.playerImages == [(PONE.md5): PONE.imageUrl, (PTWO.md5): PTWO.imageUrl]
        assert maskedGame.playerProfiles == [(PONE.md5): PONE.profileUrl, (PTWO.md5): PTWO.profileUrl]
        assert maskedGame.initiatingPlayer == PTWO.md5
        assert maskedGame.playerStates == [(PONE.md5): PlayerState.Accepted, (PTWO.md5): PlayerState.Rejected]
        assert maskedGame.maskedForPlayerID == PONE.idAsString
        assert maskedGame.maskedForPlayerMD5 == PONE.md5
    }


    protected static void checkUnmaskedGameFields(MaskedIntGame maskedGame, IntGame game) {
        assert maskedGame.id == game.idAsString
        assert maskedGame.completedTimestamp == (game.completedTimestamp ? game.completedTimestamp.toInstant().toEpochMilli() : null)
        assert maskedGame.created == (game.created ? game.created.toInstant().toEpochMilli() : null)
        assert maskedGame.declinedTimestamp == (game.declinedTimestamp ? game.declinedTimestamp.toInstant().toEpochMilli() : null)
        assert maskedGame.lastUpdate == (game.lastUpdate ? game.lastUpdate.toInstant().toEpochMilli() : null)
        assert maskedGame.features == game.features
    }

}
