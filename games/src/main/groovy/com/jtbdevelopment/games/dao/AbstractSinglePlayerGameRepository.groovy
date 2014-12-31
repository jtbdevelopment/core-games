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
interface AbstractSinglePlayerGameRepository<ID extends Serializable> extends AbstractGameRepository<ID, SinglePlayerGame<ID>> {
    List<SinglePlayerGame<ID>> findByPlayerId(final ID id);
}