package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic

import java.time.Instant

/**
 * Date: 2/19/15
 * Time: 7:20 AM
 */
@CompileStatic
abstract class AbstractMultiPlayerGameMasker<ID extends Serializable, FEATURES, U extends MultiPlayerGame<ID, Instant, FEATURES>, M extends MaskedMultiPlayerGame<FEATURES>> extends AbstractGameMasker<ID, FEATURES, U, M> implements GameMasker<ID, U, M> {
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
        playerMaskedGame.maskedForPlayerMD5 = player.md5
        playerMaskedGame.maskedForPlayerID = player.idAsString
        game.players.each {
            Player<ID> p ->
                playerMaskedGame.players[p.md5] = p.displayName
                playerMaskedGame.playerImages[p.md5] = p.imageUrl
                playerMaskedGame.playerProfiles[p.md5] = p.profileUrl
        }
        playerMaskedGame.initiatingPlayer = idMap[game.initiatingPlayer].md5
        game.playerStates.each {
            p, PlayerState state ->
                playerMaskedGame.playerStates[idMap[(ID) p].md5] = state
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    @Override
    protected void copyUnmaskedData(
            final U game,
            final M playerMaskedGame) {
        super.copyUnmaskedData(game, playerMaskedGame)
        playerMaskedGame.declinedTimestamp = convertTime((Instant) game.declinedTimestamp)
        playerMaskedGame.rematchTimestamp = convertTime((Instant) game.rematchTimestamp)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Map<ID, Player<ID>> createIDMap(final U game) {
        Map<ID, Player<ID>> idmap = [:]
        game.players.each {
            Player<ID> p ->
                idmap[p.id] = p
        }
        idmap
    }
}
