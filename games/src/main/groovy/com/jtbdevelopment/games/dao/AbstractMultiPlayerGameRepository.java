package com.jtbdevelopment.games.dao;

import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Date: 12/31/2014 Time: 5:33 PM
 */
@NoRepositoryBean
public interface AbstractMultiPlayerGameRepository<ID extends Serializable, TIMESTAMP, FEATURES, IMPL extends MultiPlayerGame<ID, TIMESTAMP, FEATURES>> extends
    AbstractGameRepository<ID, TIMESTAMP, FEATURES, IMPL> {

  List<MultiPlayerGame<ID, TIMESTAMP, FEATURES>> findByPlayersId(final ID id);

  List<MultiPlayerGame<ID, TIMESTAMP, FEATURES>> findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(
      final ID id, final GamePhase gamePhase, final TIMESTAMP cutoff, final Pageable pageable);
}
