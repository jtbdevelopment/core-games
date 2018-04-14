package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringMaskedMPGame
import com.jtbdevelopment.games.StringPlayer
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.masking.GameMasker
import org.springframework.data.domain.PageRequest

import java.time.Instant

/**
 * Date: 12/4/2014
 * Time: 9:59 PM
 */
class PlayerGamesFinderHandlerTest extends GameCoreTestCase {
    PlayerGamesFinderHandler handler = new PlayerGamesFinderHandler()

    void testTest() {
        def game1 = makeSimpleMPGame("1")
        def game2 = makeSimpleMPGame("2")
        def game3 = makeSimpleMPGame("3")
        def masked1 = new StringMaskedMPGame(id: "1")
        def masked2 = new StringMaskedMPGame(id: "2")
        def masked3 = new StringMaskedMPGame(id: "3")
        def queryResults = [
                (GamePhase.Challenged)      : [game1],
                (GamePhase.Declined)        : [],
                (GamePhase.NextRoundStarted): [game2],
                (GamePhase.Playing)         : [],
                (GamePhase.Quit)            : [],
                (GamePhase.RoundOver)       : [game3],
                (GamePhase.Setup)           : [],
        ]
        def maskResults = [
                (game1): masked1,
                (game2): masked2,
                (game3): masked3
        ]
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository<String>

        handler.gameRepository = [
                findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan: {
                    String id, GamePhase gp, Instant dt, PageRequest pr ->
                        return queryResults[gp]
                }
        ] as AbstractMultiPlayerGameRepository
        handler.gameMasker = [
                maskGameForPlayer: {
                    MultiPlayerGame game, StringPlayer player ->
                        assert player.is(PONE)
                        return maskResults[game]
                }
        ] as GameMasker

        assert handler.findGames(PONE.id) as Set == [masked3, masked2, masked1] as Set
    }
}
