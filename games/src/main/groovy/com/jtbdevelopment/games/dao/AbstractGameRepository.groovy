package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.games.Game
import groovy.transform.CompileStatic
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Date: 12/31/2014
 * Time: 5:32 PM
 *
 *  TODO - cache
 *
 */
@CompileStatic
@NoRepositoryBean
interface AbstractGameRepository<ID extends Serializable, TIMESTAMP, FEATURES, IMPL extends Game<ID, TIMESTAMP, FEATURES>> extends PagingAndSortingRepository<IMPL, ID> {
    @CachePut(value = 'game-LHC', key = '#result.id')
    IMPL save(IMPL entity)

    @CachePut(value = 'game-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.GameKeyUtility).collectGameIDs(#result)')
    Iterable<IMPL> save(Iterable<IMPL> entities)

    @Override
    @Cacheable(value = 'game-LHC')
    IMPL findOne(ID id)

    @Override
    @CacheEvict(value = 'game-LHC')
    void delete(ID id)

    @Override
    @CacheEvict(value = 'game-LHC', key = '#p0.id')
    void delete(IMPL entity)

    @Override
    @CacheEvict(value = 'game-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.GameKeyUtility).collectGameIDs(#p0)')
    void delete(Iterable<? extends IMPL> entities)

    @Override
    @CacheEvict(value = 'game-LHC', allEntries = true)
    void deleteAll()
}
