package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.SinglePlayerGame
import groovy.transform.CompileStatic
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.NoRepositoryBean

import java.time.Instant

/**
 * Date: 12/31/2014
 * Time: 5:33 PM
 */
@CompileStatic
@NoRepositoryBean
interface AbstractSinglePlayerGameRepository<ID extends Serializable, TIMESTAMP, FEATURES, IMPL extends SinglePlayerGame<ID, TIMESTAMP, FEATURES>> extends AbstractGameRepository<ID, TIMESTAMP, FEATURES, IMPL> {
    List<SinglePlayerGame<ID, TIMESTAMP, FEATURES>> findByPlayerId(final ID id)

    List<SinglePlayerGame<ID, TIMESTAMP, FEATURES>> findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(
            final ID id, final GamePhase gamePhase, final Instant cutoff, final Pageable pageable)
}