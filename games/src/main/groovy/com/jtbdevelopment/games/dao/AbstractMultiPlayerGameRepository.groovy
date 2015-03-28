package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.state.MultiPlayerGame
import groovy.transform.CompileStatic
import org.springframework.data.repository.NoRepositoryBean

/**
 * Date: 12/31/2014
 * Time: 5:33 PM
 */
@CompileStatic
@NoRepositoryBean
interface AbstractMultiPlayerGameRepository<ID extends Serializable, TIMESTAMP, FEATURES, IMPL extends MultiPlayerGame<ID, TIMESTAMP, FEATURES>> extends AbstractGameRepository<ID, TIMESTAMP, FEATURES, IMPL> {
    List<MultiPlayerGame<ID, TIMESTAMP, FEATURES>> findByPlayersId(final ID id);
}