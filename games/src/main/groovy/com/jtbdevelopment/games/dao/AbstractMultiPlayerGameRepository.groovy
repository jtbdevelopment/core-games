package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.games.MultiPlayerGame
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
interface AbstractMultiPlayerGameRepository<ID extends Serializable, IMPL extends MultiPlayerGame<ID>> extends AbstractGameRepository<ID, IMPL> {
    List<MultiPlayerGame<ID>> findByPlayersId(final ID id);
}