package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.games.SinglePlayerGame
import groovy.transform.CompileStatic
import org.springframework.data.repository.NoRepositoryBean

/**
 * Date: 12/31/2014
 * Time: 5:33 PM
 *
 * TODO - cache
 *
 */
@CompileStatic
@NoRepositoryBean
interface AbstractSinglePlayerGameRepository<ID extends Serializable, TIMESTAMP, FEATURES, IMPL extends SinglePlayerGame<ID, TIMESTAMP, FEATURES>> extends AbstractGameRepository<ID, TIMESTAMP, FEATURES, IMPL> {
    List<SinglePlayerGame<ID, TIMESTAMP, FEATURES>> findByPlayerId(final ID id);
}