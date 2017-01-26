package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.SinglePlayerGame
import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 2/19/15
 * Time: 7:20 AM
 */
@CompileStatic
abstract class AbstractSinglePlayerGameMasker<ID extends Serializable, FEATURES, U extends SinglePlayerGame<ID, ZonedDateTime, FEATURES>, M extends MaskedSinglePlayerGame<FEATURES>> extends AbstractGameMasker<ID, FEATURES, U, M> implements GameMasker<ID, U, M> {
    @Override
    M maskGameForPlayer(final U game, final Player<ID> player) {
        M playerMaskedGame = (M) super.maskGameForPlayer(game, player)

        copyUnmaskedData(game, playerMaskedGame)

        Map<ID, Player<ID>> idMap = createIDMap(game)
        copyMaskedData(game, player, playerMaskedGame, idMap)

        playerMaskedGame
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @Override
    protected void copyMaskedData(
            final U game,
            final Player<ID> player,
            final M playerMaskedGame,
            final Map<ID, Player<ID>> idMap) {
        super.copyMaskedData(game, player, playerMaskedGame, idMap)
        playerMaskedGame.players[player.md5] = player.displayName
        playerMaskedGame.playerImages[player.md5] = player.imageUrl
        playerMaskedGame.playerProfiles[player.md5] = player.profileUrl
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    @Override
    protected void copyUnmaskedData(
            final U game,
            final M playerMaskedGame) {
        super.copyUnmaskedData(game, playerMaskedGame)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Map<ID, Player<ID>> createIDMap(final U game) {
        Map<ID, Player<ID>> idmap = [:]
        game.player.each {
            Player<ID> p ->
                idmap[p.id] = p
        }
        idmap
    }
}
