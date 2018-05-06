package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository;
import com.jtbdevelopment.games.mongo.state.AbstractMongoSinglePlayerGame;
import org.bson.types.ObjectId;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Date: 1/9/15 Time: 10:51 PM
 */
@NoRepositoryBean
public interface AbstractMongoSinglePlayerGameRepository<FEATURES, IMPL extends AbstractMongoSinglePlayerGame<FEATURES>>
    extends AbstractSinglePlayerGameRepository<ObjectId, FEATURES, IMPL> {

}
