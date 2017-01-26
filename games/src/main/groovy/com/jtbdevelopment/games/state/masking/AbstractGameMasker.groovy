package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 2/19/15
 * Time: 7:20 AM
 */
@CompileStatic
abstract class AbstractGameMasker<ID extends Serializable, FEATURES, U extends Game<ID, ZonedDateTime, FEATURES>, M extends MaskedGame<FEATURES>> implements GameMasker<ID, U, M> {
    abstract protected M newMaskedGame()

    abstract protected Map<ID, Player<ID>> createIDMap(final U game)

    abstract Class<ID> getIDClass()

    @Override
    M maskGameForPlayer(final U game, final Player<ID> player) {
        M playerMaskedGame = newMaskedGame()

        copyUnmaskedData(game, playerMaskedGame)

        Map<ID, Player<ID>> idMap = createIDMap(game)
        copyMaskedData(game, player, playerMaskedGame, idMap)

        playerMaskedGame
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    protected void copyMaskedData(
            final U game,
            final Player<ID> player,
            final M playerMaskedGame,
            final Map<ID, Player<ID>> idMap) {
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
            final U game,
            final M playerMaskedGame) {
        playerMaskedGame.completedTimestamp = convertTime((ZonedDateTime) game.completedTimestamp)
        playerMaskedGame.created = convertTime((ZonedDateTime) game.created)
        playerMaskedGame.lastUpdate = convertTime((ZonedDateTime) game.lastUpdate)
        playerMaskedGame.features.addAll(game.features)
        playerMaskedGame.id = game.idAsString
        playerMaskedGame.gamePhase = game.gamePhase
        playerMaskedGame.round = game.round
        playerMaskedGame.previousId = game.previousIdAsString
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Long convertTime(final ZonedDateTime value) {
        value ? value.toInstant().toEpochMilli() : null
    }
}
