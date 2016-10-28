package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame
import com.jtbdevelopment.games.state.masking.MultiPlayerGameMasker
import groovy.transform.CompileStatic
import groovyx.gpars.GParsPool
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 11/19/14
 * Time: 7:08 AM
 */
@Component
@CompileStatic
class PlayerGamesFinderHandler extends AbstractGameGetterHandler {
    private static int DEFAULT_PAGE_SIZE = 20;
    private static int DEFAULT_PAGE = 0;
    public static final ZoneId GMT = ZoneId.of("GMT")
    public static final Sort SORT = new Sort(Sort.Direction.DESC, ["lastUpdate", "created"])
    public static final PageRequest PAGE = new PageRequest(DEFAULT_PAGE, DEFAULT_PAGE_SIZE, SORT)

    @Autowired
    protected MultiPlayerGameMasker gameMasker

    public List<MaskedMultiPlayerGame> findGames(final Serializable playerID) {
        Player player = loadPlayer(playerID);
        ZonedDateTime now = ZonedDateTime.now(GMT)

        List<MaskedMultiPlayerGame> result = [];
        GParsPool.withPool {
            GamePhase.values().each {
                GamePhase phase ->
                    def days = now.minusDays(phase.historyCutoffDays)
                    result.addAll(((AbstractMultiPlayerGameRepository) gameRepository).findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(
                            player.id,
                            phase,
                            days,
                            PAGE
                    ).collect {
                        game ->
                            gameMasker.maskGameForPlayer(game, player)
                    })
            }
        }
        result
    }
}
