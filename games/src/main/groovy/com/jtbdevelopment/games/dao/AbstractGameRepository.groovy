package com.jtbdevelopment.games.dao

import com.jtbdevelopment.games.games.Game
import groovy.transform.CompileStatic
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
}
