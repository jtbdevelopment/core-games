package com.jtbdevelopment.games.games.masked

import com.jtbdevelopment.games.games.MultiPlayerGame
import com.jtbdevelopment.games.games.PlayerState
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 2/19/15
 * Time: 7:20 AM
 */
@CompileStatic
abstract class AbstractMultiPlayerGameMasker<ID extends Serializable, FEATURES, U extends MultiPlayerGame<ID, ZonedDateTime, FEATURES>, M extends MaskedMultiPlayerGame<FEATURES>> implements MultiPlayerGameMasker<ID, U, M> {
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
            final U game, final Player<ID> player, final M playerMaskedGame, Map<ID, Player<ID>> idMap) {
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

    protected static Map<ID, Player<ID>> createIDMap(final U game) {
        Map<ID, Player<ID>> idmap = [:]
        game.players.each {
            Player<ID> p ->
                idmap[p.id] = p
        }
        idmap
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void copyUnmaskedData(final U game, final M playerMaskedGame) {
        playerMaskedGame.completedTimestamp = convertTime((ZonedDateTime) game.completedTimestamp)
        playerMaskedGame.created = convertTime((ZonedDateTime) game.created)
        playerMaskedGame.declinedTimestamp = convertTime((ZonedDateTime) game.declinedTimestamp)
        playerMaskedGame.lastUpdate = convertTime((ZonedDateTime) game.lastUpdate)
        playerMaskedGame.features.addAll(game.features)
        playerMaskedGame.id = game.idAsString
    }

    protected static Long convertTime(final ZonedDateTime value) {
        value ? value.toInstant().toEpochMilli() : null
    }
}
