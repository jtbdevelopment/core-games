package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

/**
 * Date: 12/30/2014 Time: 11:06 AM
 */
@Repository
public interface MongoPlayerRepository extends AbstractPlayerRepository<ObjectId, MongoPlayer> {

}
