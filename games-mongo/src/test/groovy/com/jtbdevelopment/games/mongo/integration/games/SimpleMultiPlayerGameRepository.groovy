package com.jtbdevelopment.games.mongo.integration.games

import com.jtbdevelopment.games.mongo.dao.AbstractMongoMultiPlayerGameRepository
import groovy.transform.CompileStatic

/**
 * Date: 1/10/15
 * Time: 2:42 PM
 */
@CompileStatic
interface SimpleMultiPlayerGameRepository extends AbstractMongoMultiPlayerGameRepository<SimpleMultiPlayerGame> {
}
