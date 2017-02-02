package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository

import java.time.ZonedDateTime

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
interface AbstractPlayerRepository<ID extends Serializable, P extends Player<ID>> extends PagingAndSortingRepository<P, ID> {

    @Cacheable(value = PLAYER_ID_CACHE)
    P findOne(ID id)

    @Cacheable(value = PLAYER_MD5_CACHE)
    P findByMd5(final String md5)

    @Cacheable(value = PLAYER_MD5_CACHE)
    List<P> findByMd5In(final Collection<String> md5s)

    @Cacheable(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.players.AbstractPlayer).getSourceAndSourceId(#p0, #p1)')
    P findBySourceAndSourceId(final String source, final String sourceId)

    @Cacheable(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectSourceAndSourceIDs(#p0, #p1)')
    List<P> findBySourceAndSourceIdIn(final String source, final Collection<String> sourceId)

    //  Not caching - should mostly be used on startup to create system players
    P findByDisplayName(final String displayName)

    //  Not caching - should mostly be used by admin player search
    Page<P> findByDisplayNameContains(final String displayName, Pageable pageable)

    //  Not caching - currently only used by manual players for testing
    List<P> findBySourceAndDisabled(final String source, final boolean disabled)

    //  Not caching - loading in order to delete
    List<P> findByLastLoginLessThan(final ZonedDateTime cutoff)

    @Caching(
            evict = [
                    @CacheEvict(value = PLAYER_ID_CACHE, allEntries = true),
                    @CacheEvict(value = PLAYER_MD5_CACHE, allEntries = true),
                    @CacheEvict(value = PLAYER_S_AND_SID_CACHE, allEntries = true),
            ]
    )
    long deleteByLastLoginLessThan(final ZonedDateTime cutoff)

    @Caching(
            put = [
                    @CachePut(value = PLAYER_ID_CACHE, key = '#result.id'),
                    @CachePut(value = PLAYER_MD5_CACHE, key = '#result.md5'),
                    @CachePut(value = PLAYER_S_AND_SID_CACHE, key = '#result.sourceAndSourceId')
            ]
    )
    P save(P entity)

    @Caching(
            put = [
                    @CachePut(value = PLAYER_ID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerIDs(#result)'),
                    @CachePut(value = PLAYER_MD5_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerMD5s(#result)'),
                    @CachePut(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerSourceAndSourceIDs(#result)')
            ]
    )
    Iterable<P> save(Iterable<P> entities)

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
    void delete(P entity)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = PLAYER_ID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerIDs(#p0)'),
                    @CacheEvict(value = PLAYER_MD5_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerMD5s(#p0)'),
                    @CacheEvict(value = PLAYER_S_AND_SID_CACHE, key = 'T(com.jtbdevelopment.games.dao.caching.PlayerKeyUtility).collectPlayerSourceAndSourceIDs(#p0)')
            ]
    )
    void delete(Iterable<? extends P> entities)

    @Override
    @Caching(
            evict = [
                    @CacheEvict(value = PLAYER_ID_CACHE, allEntries = true),
                    @CacheEvict(value = PLAYER_MD5_CACHE, allEntries = true),
                    @CacheEvict(value = PLAYER_S_AND_SID_CACHE, allEntries = true)
            ]
    )
    void deleteAll()

    long countByCreatedGreaterThan(final ZonedDateTime cutoff)

    long countByLastLoginGreaterThan(final ZonedDateTime cutoff)
}