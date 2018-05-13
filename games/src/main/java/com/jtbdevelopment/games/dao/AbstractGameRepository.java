package com.jtbdevelopment.games.dao;

import static com.jtbdevelopment.games.dao.caching.CacheConstants.GAME_ID_CACHE;

import com.jtbdevelopment.games.state.AbstractGame;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Date: 12/31/2014 Time: 5:32 PM
 */
@SuppressWarnings("SpringCacheAnnotationsOnInterfaceInspection")
@NoRepositoryBean
public interface AbstractGameRepository<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>>
    extends PagingAndSortingRepository<IMPL, ID> {

  @Override
  @CachePut(value = GAME_ID_CACHE, key = "#result.id")
  <S extends IMPL> S save(S entity);

  @Override
  @CachePut(value = GAME_ID_CACHE, key = "T(com.jtbdevelopment.games.dao.caching.GameKeyUtility).collectGameIDs(#result)")
  <S extends IMPL> Iterable<S> saveAll(Iterable<S> entities);

  @Override
  @Cacheable(value = GAME_ID_CACHE)
  Optional<IMPL> findById(ID id);

  @Override
  @CacheEvict(value = GAME_ID_CACHE, key = "#p0.id")
  void delete(IMPL entity);

  @Override
  @CacheEvict(value = GAME_ID_CACHE, key = "#p0")
  void deleteById(ID id);

  @Override
  @CacheEvict(value = GAME_ID_CACHE, key = "T(com.jtbdevelopment.games.dao.caching.GameKeyUtility).collectGameIDs(#p0)")
  void deleteAll(Iterable<? extends IMPL> entities);

  @Override
  @CacheEvict(value = GAME_ID_CACHE, allEntries = true)
  void deleteAll();

  long countByCreatedGreaterThan(final Instant cutoff);

  List<IMPL> findByCreatedLessThan(final Instant cutoff);

  @CacheEvict(value = GAME_ID_CACHE, allEntries = true)
  long deleteByCreatedLessThan(final Instant cutoff);
}
