package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.players.AbstractPlayer
import com.jtbdevelopment.games.state.AbstractSinglePlayerGame
import com.jtbdevelopment.games.state.GamePhase

import java.time.Instant

/**
 * Date: 2/19/15
 * Time: 6:30 PM
 */
//  Not using GameCoreTestCase in order to use Integer keys as part of testing
class AbstractSinglePlayerGameMaskerTest extends GroovyTestCase {
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

    private static class IntGame extends AbstractSinglePlayerGame<Integer, Features> {
        Integer id
        Integer previousId

        @Override
        String getIdAsString() {
            return id?.toString()
        }

        @Override
        String getPreviousIdAsString() {
            return previousId?.toString()
        }
    }

    private static class MaskedIntGame extends AbstractMaskedSinglePlayerGame<Features> {
    }

    static class MaskedIntGameMasker extends AbstractSinglePlayerGameMasker<Integer, Features, IntGame, MaskedIntGame> {
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

    void testMaskingSinglePlayerGame() {
        IntGame game = new IntGame(
                gamePhase: GamePhase.Quit,
                player: PONE,
                created: Instant.now(),
                completedTimestamp: Instant.now(),
                featureData: [(Features.FeatureA): ""],
                features: [Features.FeatureA, Features.FeatureB] as Set,
                previousId: 100,
                id: 101,
                lastUpdate: Instant.now(),
                version: 10,
        )

        MaskedIntGame maskedGame = masker.maskGameForPlayer(game, PONE)
        checkUnmaskedGameFields(maskedGame, game)

        assert maskedGame.players == [(PONE.md5): PONE.displayName]
        assert maskedGame.playerImages == [(PONE.md5): PONE.imageUrl]
        assert maskedGame.playerProfiles == [(PONE.md5): PONE.profileUrl]
        assert maskedGame.featureData == game.featureData
    }


    protected static void checkUnmaskedGameFields(MaskedIntGame maskedGame, IntGame game) {
        assert maskedGame.id == game.idAsString
        assert game.previousIdAsString == maskedGame.previousId
        assert maskedGame.completedTimestamp == (game.completedTimestamp ? game.completedTimestamp.toEpochMilli() : null)
        assert maskedGame.created == (game.created ? game.created.toEpochMilli() : null)
        assert maskedGame.lastUpdate == (game.lastUpdate ? game.lastUpdate.toEpochMilli() : null)
        assert maskedGame.features == game.features
        assert maskedGame.gamePhase == game.gamePhase
        assert maskedGame.round == game.round
    }

}
