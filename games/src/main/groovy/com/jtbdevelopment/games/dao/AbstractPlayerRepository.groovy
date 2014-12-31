package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Date: 12/30/2014
 * Time: 11:07 AM
 */
@CompileStatic
@NoRepositoryBean
interface AbstractPlayerRepository<ID extends Serializable> extends PagingAndSortingRepository<Player<ID>, ID> {
    List<Player<ID>> findByMd5In(final Collection<String> md5s);

    Player<ID> findBySourceAndSourceId(final String source, final String sourceId);

    List<Player<ID>> findBySourceAndDisabled(final String source, final boolean disabled);
}