package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository

import static com.jtbdevelopment.games.dao.caching.CacheConstants.*

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

    @Cacheable(value = PLAYER_ID_CACHE)
    Player<ID> findOne(ID id)

    @Cacheable(value = PLAYER_MD5_CACHE)
    List<Player<ID>> findByMd5In(final Collection<String> md5s);

    @Cacheable(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.players.AbstractPlayer).getSourceAndSourceId(#p0, #p1)')
    Player<ID> findBySourceAndSourceId(final String source, final String sourceId);

    @Cacheable(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectSourceAndSourceIDs(#p0, #p1)')
    List<Player<ID>> findBySourceAndSourceIdIn(final String source, final Collection<String> sourceId);

    //  Not caching - currently only used by manual players for testing
    List<Player<ID>> findBySourceAndDisabled(final String source, final boolean disabled);

    @Caching(
            put = [
                    @CachePut(value = PLAYER_ID_CACHE, key = '#result.id'),
                    @CachePut(value = PLAYER_MD5_CACHE, key = '#result.md5'),
                    @CachePut(value = PLAYER_S_AND_SID_CACHE, key = '#result.sourceAndSourceId')
            ]
    )
    Player<ID> save(Player<ID> entity)

    @Caching(
            put = [
                    @CachePut(value = PLAYER_ID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerIDs(#result)'),
                    @CachePut(value = PLAYER_MD5_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerMD5s(#result)'),
                    @CachePut(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerSourceAndSourceIDs(#result)')
            ]
    )
    Iterable<Player<ID>> save(Iterable<Player<ID>> entities)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = PLAYER_ID_CACHE, key = '#p0'),
                    @CacheEvict(value = PLAYER_MD5_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).md5FromID(#p0)', beforeInvocation = true),
                    @CacheEvict(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).sourceAndSourceIDFromID(#p0)', beforeInvocation = true)
            ]
    )
    void delete(ID id)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = PLAYER_ID_CACHE, key = '#p0.id'),
                    @CacheEvict(value = PLAYER_MD5_CACHE, key = '#p0.md5'),
                    @CacheEvict(value = PLAYER_S_AND_SID_CACHE, key = '#p0.sourceAndSourceId')
            ]
    )
    void delete(Player<ID> entity)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = PLAYER_ID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerIDs(#p0)'),
                    @CacheEvict(value = PLAYER_MD5_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerMD5s(#p0)'),
                    @CacheEvict(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerSourceAndSourceIDs(#p0)')
            ]
    )
    void delete(Iterable<? extends Player<ID>> entities)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = PLAYER_ID_CACHE, allEntries = true),
                    @CacheEvict(value = PLAYER_MD5_CACHE, allEntries = true),
                    @CacheEvict(value = PLAYER_S_AND_SID_CACHE, allEntries = true)
            ]
    )
    void deleteAll()
}