package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository

import static com.jtbdevelopment.games.dao.caching.CacheConstants.GAME_ID_CACHE

/**
 * Date: 12/31/2014
 * Time: 5:32 PM
 */
@CompileStatic
@NoRepositoryBean
interface AbstractGameRepository<ID extends Serializable, TIMESTAMP, FEATURES, IMPL extends Game<ID, TIMESTAMP, FEATURES>> extends PagingAndSortingRepository<IMPL, ID> {

    @CachePut(value = GAME_ID_CACHE, key = '#result.id')
    IMPL save(IMPL entity)

    @CachePut(value = GAME_ID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.GameKeyUtility).collectGameIDs(#result)')
    Iterable<IMPL> save(Iterable<IMPL> entities)

    @Override
    @Cacheable(value = GAME_ID_CACHE)
    IMPL findOne(ID id)

    @Override
    @CacheEvict(value = GAME_ID_CACHE)
    void delete(ID id)

    @Override
    @CacheEvict(value = GAME_ID_CACHE, key = '#p0.id')
    void delete(IMPL entity)

    @Override
    @CacheEvict(value = GAME_ID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.GameKeyUtility).collectGameIDs(#p0)')
    void delete(Iterable<? extends IMPL> entities)

    @Override
    @CacheEvict(value = GAME_ID_CACHE, allEntries = true)
    void deleteAll()

    long countByCreatedGreaterThan(final TIMESTAMP cutoff)

    //  Not caching - likely for maintenance
    List<IMPL> findByCreatedLessThan(final TIMESTAMP cutoff)

    @CacheEvict(value = GAME_ID_CACHE, allEntries = true)
    long deleteByCreatedLessThan(final TIMESTAMP cutoff)
}
