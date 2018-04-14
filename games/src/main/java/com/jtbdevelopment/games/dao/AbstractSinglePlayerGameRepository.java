package com.jtbdevelopment.games.dao;

import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Date: 12/31/2014 Time: 5:33 PM
 */
@NoRepositoryBean
public interface AbstractSinglePlayerGameRepository<ID extends Serializable, TIMESTAMP, FEATURES, IMPL extends SinglePlayerGame<ID, TIMESTAMP, FEATURES>> extends
    AbstractGameRepository<ID, TIMESTAMP, FEATURES, IMPL> {

  List<SinglePlayerGame<ID, TIMESTAMP, FEATURES>> findByPlayerId(final ID id);

  List<SinglePlayerGame<ID, TIMESTAMP, FEATURES>> findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(
      final ID id, final GamePhase gamePhase, final TIMESTAMP cutoff, final Pageable pageable);
}
