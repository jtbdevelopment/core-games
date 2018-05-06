package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.mongo.state.AbstractMongoMultiPlayerGame;
import org.bson.types.ObjectId;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Date: 1/9/15 Time: 10:51 PM
 */
@NoRepositoryBean
public interface AbstractMongoMultiPlayerGameRepository<FEATURES, IMPL extends AbstractMongoMultiPlayerGame<FEATURES>>
    extends AbstractMultiPlayerGameRepository<ObjectId, FEATURES, IMPL> {

}
