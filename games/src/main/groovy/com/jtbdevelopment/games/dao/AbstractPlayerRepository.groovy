package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Date: 12/30/2014
 * Time: 11:07 AM
 *
 * If using a dynamic language like groovy, you may need to compile statically
 * due to some of the overloaded methods on delete not being resolvable at runtime
 * since both ID and Player implement Serializable
 *
 */
@CompileStatic
@NoRepositoryBean
interface AbstractPlayerRepository<ID extends Serializable> extends PagingAndSortingRepository<Player<ID>, ID> {

    @Cacheable(value = 'playerID-LHC')
    Player<ID> findOne(ID id)

    @Cacheable(value = 'playerMD5-LHC')
    List<Player<ID>> findByMd5In(final Collection<String> md5s);

    @Cacheable(value = 'playerSSID-LHC', key = '#p0 + "/" + #p1')
    Player<ID> findBySourceAndSourceId(final String source, final String sourceId);

    @Cacheable(value = 'playerSSID-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectSourceAndSourceIDs(#p0, #p1)')
    List<Player<ID>> findBySourceAndSourceIdIn(final String source, final Collection<String> sourceId);

    //  Not caching - currently only used by manual players for testing
    List<Player<ID>> findBySourceAndDisabled(final String source, final boolean disabled);

    @Caching(
            put = [
                    @CachePut(value = 'playerID-LHC', key = '#result.id'),
                    @CachePut(value = 'playerMD5-LHC', key = '#result.md5'),
                    @CachePut(value = 'playerSSID-LHC', key = '#result.source+"/"+#result.sourceId')
            ]
    )
    Player<ID> save(Player<ID> entity)

    @Caching(
            put = [
                    @CachePut(value = 'playerID-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerIDs(#result)'),
                    @CachePut(value = 'playerMD5-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerMD5s(#result)'),
                    @CachePut(value = 'playerSSID-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerSourceAndSourceIDs(#result)')
            ]
    )
    Iterable<Player<ID>> save(Iterable<Player<ID>> entities)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = 'playerID-LHC', key = '#p0'),
                    @CacheEvict(value = 'playerMD5-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).md5FromID(#p0)', beforeInvocation = true),
                    @CacheEvict(value = 'playerSSID-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).sourceAndSourceIDFromID(#p0)', beforeInvocation = true)
            ]
    )
    void delete(ID id)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = 'playerID-LHC', key = '#p0.id'),
                    @CacheEvict(value = 'playerMD5-LHC', key = '#p0.md5'),
                    @CacheEvict(value = 'playerSSID-LHC', key = '#p0.source+"/"+#p0.sourceId')
            ]
    )
    void delete(Player<ID> entity)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = 'playerID-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerIDs(#p0)'),
                    @CacheEvict(value = 'playerMD5-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerMD5s(#p0)'),
                    @CacheEvict(value = 'playerSSID-LHC', key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerSourceAndSourceIDs(#p0)')
            ]
    )
    void delete(Iterable<? extends Player<ID>> entities)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = 'playerID-LHC', allEntries = true),
                    @CacheEvict(value = 'playerMD5-LHC', allEntries = true),
                    @CacheEvict(value = 'playerSSID-LHC', allEntries = true)
            ]
    )
    void deleteAll()
}