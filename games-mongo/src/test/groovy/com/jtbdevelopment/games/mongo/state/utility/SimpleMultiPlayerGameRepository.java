package com.jtbdevelopment.games.mongo.state.utility;

import com.jtbdevelopment.games.mongo.dao.AbstractMongoMultiPlayerGameRepository;
import groovy.transform.CompileStatic;

/**
 * Date: 1/10/15 Time: 2:42 PM
 */
@CompileStatic
public interface SimpleMultiPlayerGameRepository extends
    AbstractMongoMultiPlayerGameRepository<String, SimpleMultiPlayerGame> {

}
