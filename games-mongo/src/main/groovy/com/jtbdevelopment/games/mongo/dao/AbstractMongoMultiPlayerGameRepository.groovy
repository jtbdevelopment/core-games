package com.jtbdevelopment.games.mongo.dao

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.mongo.state.AbstractMongoMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.repository.NoRepositoryBean

import java.time.Instant

/**
 * Date: 1/9/15
 * Time: 10:51 PM
 */
@CompileStatic
@NoRepositoryBean
interface AbstractMongoMultiPlayerGameRepository<FEATURES, IMPL extends AbstractMongoMultiPlayerGame<FEATURES>> extends AbstractMultiPlayerGameRepository<ObjectId, Instant, FEATURES, IMPL> {
}
