package com.jtbdevelopment.games.mongo.dao

import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository
import com.jtbdevelopment.games.mongo.games.AbstractMongoSinglePlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.repository.NoRepositoryBean

import java.time.ZonedDateTime

/**
 * Date: 1/9/15
 * Time: 10:51 PM
 */
@CompileStatic
@NoRepositoryBean
interface AbstractMongoSinglePlayerGameRepository<IMPL extends AbstractMongoSinglePlayerGame> extends AbstractSinglePlayerGameRepository<ObjectId, ZonedDateTime, IMPL> {
}
