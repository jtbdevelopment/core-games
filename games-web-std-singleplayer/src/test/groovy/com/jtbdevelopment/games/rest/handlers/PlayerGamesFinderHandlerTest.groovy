package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringMaskedSPGame
import com.jtbdevelopment.games.StringPlayer
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.SinglePlayerGame
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
        def game1 = makeSimpleSPGame("1")
        def game2 = makeSimpleSPGame("2")
        def game3 = makeSimpleSPGame("3")
        def masked1 = new StringMaskedSPGame(id: "1")
        def masked2 = new StringMaskedSPGame(id: "2")
        def masked3 = new StringMaskedSPGame(id: "3")
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
                findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan: {
                    String id, GamePhase gp, Instant dt, PageRequest pr ->
                        return queryResults[gp]
                }
        ] as AbstractSinglePlayerGameRepository
        handler.gameMasker = [
                maskGameForPlayer: {
                    SinglePlayerGame game, StringPlayer player ->
                        assert player.is(PONE)
                        return maskResults[game]
                }
        ] as GameMasker

        assert handler.findGames(PONE.id) as Set == [masked3, masked2, masked1] as Set
    }
}
