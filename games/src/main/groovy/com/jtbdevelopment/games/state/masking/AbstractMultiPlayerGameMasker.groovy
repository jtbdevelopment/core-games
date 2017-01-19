package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 2/19/15
 * Time: 7:20 AM
 */
@CompileStatic
abstract class AbstractMultiPlayerGameMasker<ID extends Serializable, FEATURES, U extends MultiPlayerGame<ID, ZonedDateTime, FEATURES>, M extends MaskedMultiPlayerGame<FEATURES>> implements GameMasker<ID, U, M> {
    abstract protected M newMaskedGame()

    abstract Class<ID> getIDClass()

    @Override
    M maskGameForPlayer(final U game, final Player<ID> player) {
        M playerMaskedGame = newMaskedGame()

        playerMaskedGame.maskedForPlayerID = player.idAsString
        playerMaskedGame.maskedForPlayerMD5 = player.md5

        copyUnmaskedData(game, playerMaskedGame)

        Map<ID, Player<ID>> idMap = createIDMap(game)
        copyMaskedData(game, player, playerMaskedGame, idMap)

        playerMaskedGame
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    protected void copyMaskedData(
            final MultiPlayerGame<ID, ZonedDateTime, FEATURES> game,
            final Player<ID> player,
            final MaskedMultiPlayerGame<FEATURES> playerMaskedGame,
            final Map<ID, Player<ID>> idMap) {
        game.players.each {
            Player<ID> p ->
                playerMaskedGame.players[p.md5] = p.displayName
                playerMaskedGame.playerImages[p.md5] = p.imageUrl
                playerMaskedGame.playerProfiles[p.md5] = p.profileUrl
        }
        playerMaskedGame.initiatingPlayer = idMap[game.initiatingPlayer].md5
        game.playerStates.each {
            def p, PlayerState state ->
                playerMaskedGame.playerStates[idMap[(ID) p].md5] = state
        }
        Class<ID> idClass = getIDClass()
        game.featureData.each {
            FEATURES feature, Object data ->
                playerMaskedGame.featureData[feature] =
                        (idClass.is(data.class) && idMap.containsKey(data)) ?
                                idMap[(ID) data].md5 :
                                data
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void copyUnmaskedData(
            final MultiPlayerGame<ID, ZonedDateTime, FEATURES> game,
            final MaskedMultiPlayerGame<FEATURES> playerMaskedGame) {
        playerMaskedGame.completedTimestamp = convertTime(game.completedTimestamp)
        playerMaskedGame.created = convertTime(game.created)
        playerMaskedGame.declinedTimestamp = convertTime(game.declinedTimestamp)
        playerMaskedGame.rematchTimestamp = convertTime(game.rematchTimestamp)
        playerMaskedGame.lastUpdate = convertTime(game.lastUpdate)
        playerMaskedGame.features.addAll(game.features)
        playerMaskedGame.id = game.idAsString
        playerMaskedGame.gamePhase = game.gamePhase
        playerMaskedGame.round = game.round
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

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Long convertTime(final ZonedDateTime value) {
        value ? value.toInstant().toEpochMilli() : null
    }
}
