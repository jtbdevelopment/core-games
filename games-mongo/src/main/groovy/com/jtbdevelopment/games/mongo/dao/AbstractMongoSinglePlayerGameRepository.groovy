package com.jtbdevelopment.games.mongo.dao

import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository
import com.jtbdevelopment.games.mongo.state.AbstractMongoSinglePlayerGame
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
interface AbstractMongoSinglePlayerGameRepository<FEATURES, IMPL extends AbstractMongoSinglePlayerGame<FEATURES>> extends AbstractSinglePlayerGameRepository<ObjectId, Instant, FEATURES, IMPL> {
}
