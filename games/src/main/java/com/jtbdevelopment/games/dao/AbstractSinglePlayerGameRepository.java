package com.jtbdevelopment.games.dao;

import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Date: 12/31/2014 Time: 5:33 PM
 */
@NoRepositoryBean
public interface AbstractSinglePlayerGameRepository<ID extends Serializable, FEATURES, IMPL extends AbstractSinglePlayerGame<ID, FEATURES>>
    extends AbstractGameRepository<ID, FEATURES, IMPL> {

  List<IMPL> findByPlayerId(final ID id);

  List<IMPL> findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(
      final ID id, final GamePhase gamePhase, final Instant cutoff, final Pageable pageable);
}
