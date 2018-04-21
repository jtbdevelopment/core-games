package com.jtbdevelopment.games.mongo.state.utility;

import com.jtbdevelopment.games.mongo.dao.AbstractMongoSinglePlayerGameRepository;
import groovy.transform.CompileStatic;

/**
 * Date: 1/10/15 Time: 2:42 PM
 */
@CompileStatic
public interface SimpleSinglePlayerGameRepository extends
    AbstractMongoSinglePlayerGameRepository<String, SimpleSinglePlayerGame> {

}
