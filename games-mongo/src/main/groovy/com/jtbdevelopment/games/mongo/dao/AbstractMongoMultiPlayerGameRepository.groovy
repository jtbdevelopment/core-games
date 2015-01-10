package com.jtbdevelopment.games.mongo.dao

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.mongo.games.AbstractMongoMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId

/**
 * Date: 1/9/15
 * Time: 10:51 PM
 */
@CompileStatic
interface AbstractMongoMultiPlayerGameRepository<IMPL extends AbstractMongoMultiPlayerGame> extends AbstractMultiPlayerGameRepository<ObjectId, IMPL> {
}
