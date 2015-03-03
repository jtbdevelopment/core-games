package com.jtbdevelopment.games.mongo.players

import com.fasterxml.jackson.databind.ObjectMapper
import com.jtbdevelopment.core.mongo.spring.AbstractMongoIntegration
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerPayLevel
import com.mongodb.DBCollection
import groovy.transform.CompileStatic
import org.junit.Before
import org.junit.Test
import org.springframework.cache.CacheManager
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * Date: 1/11/15
 * Time: 3:09 PM
 */
@CompileStatic
class MongoPlayerIntegration extends AbstractMongoIntegration {
    private static final String PLAYER_COLLECTION_NAME = 'player'
    private DBCollection collection

    MongoPlayerRepository playerRepository
    MongoPlayer player1, player2, player3, player4
    MongoManualPlayer manualPlayer
    MongoSystemPlayer systemPlayer
    CacheManager cacheManager

    protected MongoPlayer makeSimplePlayer(final String id, final boolean disabled = false) {
        return (MongoPlayer) playerRepository.save(new MongoPlayer(
                source: "MADEUP",
                sourceId: "MADEUP" + id,
                displayName: id,
                disabled: disabled,
                imageUrl: "http://somewhere.com/image/" + id,
                profileUrl: "http://somewhere.com/profile/" + id))
    }

    @Before
    void setup() {
        assert db.collectionExists(PLAYER_COLLECTION_NAME)
        collection = db.getCollection(PLAYER_COLLECTION_NAME)

        playerRepository = context.getBean(MongoPlayerRepository.class)
        playerRepository.deleteAll()

        player1 = makeSimplePlayer('1')
        player1.payLevel = PlayerPayLevel.PremiumPlayer
        player2 = makeSimplePlayer('2')
        player3 = makeSimplePlayer('3', true)
        player4 = makeSimplePlayer('4')
        manualPlayer = (MongoManualPlayer) playerRepository.save(new MongoManualPlayer(
                sourceId: "MADEUP" + "M",
                displayName: "M",
                disabled: false))
        systemPlayer = (MongoSystemPlayer) playerRepository.save(new MongoSystemPlayer(
                sourceId: "MADEUP" + "S",
                displayName: "S",
                disabled: false))
        cacheManager = context.getBean(CacheManager.class)
    }

    @Test
    void testFindBySourceAndDisabled() {
        assert playerRepository.findBySourceAndDisabled(systemPlayer.source, true).isEmpty()
        assert playerRepository.findBySourceAndDisabled(systemPlayer.source, false) == [systemPlayer]
        assert playerRepository.findBySourceAndDisabled(manualPlayer.source, true).isEmpty()
        assert playerRepository.findBySourceAndDisabled(manualPlayer.source, false) == [manualPlayer]
        assert playerRepository.findBySourceAndDisabled(player1.source, false) as Set == [player1, player2, player4] as Set
        assert playerRepository.findBySourceAndDisabled(player1.source, true) == [player3]
    }

    @Test
    void testFindBySourceAndSourceIds() {
        List<Player> players = playerRepository.findBySourceAndSourceIdIn(player1.source, [player1.sourceId, player2.sourceId, 'X'])
        assert players.size() == 2
        assert players.contains(player1)
        assert players.contains(player2)
    }

    @Test
    void testFindBySourceAndSourceId() {
        assert playerRepository.findBySourceAndSourceId(player4.source, player4.sourceId) == player4
        assert playerRepository.findBySourceAndSourceId(systemPlayer.source, systemPlayer.sourceId) == systemPlayer
        assert playerRepository.findBySourceAndSourceId(manualPlayer.source, manualPlayer.sourceId) == manualPlayer
        assert playerRepository.findBySourceAndSourceId(manualPlayer.source, manualPlayer.sourceId + 'X') == null
    }

    @Test
    void testFindByMD5() {
        List<Player> players = playerRepository.findByMd5In([player1.md5, player3.md5, systemPlayer.md5])//, 'JUNK'])
        assert players.size() == 3
        assert players.contains(player1)
        assert players.contains(player3)
        assert players.contains(systemPlayer)
    }

    @Test
    void testSerialization() {
        ObjectMapper mapper = context.getBean(ObjectMapper.class)
        assert mapper.writeValueAsString(player1) == '{"source":"MADEUP","sourceId":"MADEUP1","displayName":"1","imageUrl":"http://somewhere.com/image/1","profileUrl":"http://somewhere.com/profile/1","disabled":false,"adminUser":false,"payLevel":"PremiumPlayer","gameSpecificPlayerAttributes":null,"id":"' + player1.idAsString + '","md5":"' + player1.md5 + '"}'
    }

    @Test
    void testDeserialization() {
        ObjectMapper mapper = context.getBean(ObjectMapper.class)
        MongoPlayer player = mapper.readValue(
                '{"source":"MADEUP","sourceId":"MADEUP1","displayName":"1","imageUrl":"http://somewhere.com/image/1","profileUrl":"http://somewhere.com/profile/1","disabled":false,"adminUser":true,"id":"54b656dba826d455d3eaa8a4","md5":"94026ad238c04d23e4fd1fe7efeebabf", "payLevel": "PremiumPlayer"}',
                MongoPlayer.class
        )
        assert player
        assert player.idAsString == '54b656dba826d455d3eaa8a4'
        assert player.md5 == '94026ad238c04d23e4fd1fe7efeebabf'
        assert player.payLevel == PlayerPayLevel.PremiumPlayer
        assert player.adminUser
        assert player.profileUrl == "http://somewhere.com/profile/1"
        assert player.imageUrl == "http://somewhere.com/image/1"
    }

    @Test
    void testCacheLookupFromInitialSaves() {
        def cache = cacheManager.getCache('playerID-LHC')
        assert cache.get(player1.id).get() == player1
        assert cache.get(player2.id).get() == player2
        assert cache.get('JUNK') == null
        cache = cacheManager.getCache('playerMD5-LHC')
        assert cache.get(player1.md5).get() == player1
        assert cache.get(player2.md5).get() == player2
        assert cache.get('JUNK') == null
        cache = cacheManager.getCache('playerSSID-LHC')
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1
        assert cache.get(player2.source + "/" + player2.sourceId).get() == player2
        assert cache.get('JUNK') == null
    }

    @Test
    void testCacheLookupFromBulkSaves() {
        def cache = cacheManager.getCache('playerID-LHC')
        cache.clear()
        assert cache.get(player1.id) == null
        cache = cacheManager.getCache('playerMD5-LHC')
        cache.clear()
        assert cache.get(player1.md5) == null
        cache = cacheManager.getCache('playerSSID-LHC')
        cache.clear()
        assert cache.get(player1.source + "/" + player1.sourceId) == null

        playerRepository.save([player1, player2] as List<Player>)

        cache = cacheManager.getCache('playerID-LHC')
        assert cache.get(player1.id).get() == player1
        assert cache.get(player2.id).get() == player2
        assert cache.get('JUNK') == null
        cache = cacheManager.getCache('playerMD5-LHC')
        assert cache.get(player1.md5).get() == player1
        assert cache.get(player2.md5).get() == player2
        assert cache.get('JUNK') == null
        cache = cacheManager.getCache('playerSSID-LHC')
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1
        assert cache.get(player2.source + "/" + player2.sourceId).get() == player2
        assert cache.get('JUNK') == null
    }

    @Test
    void testCacheDeleteAll() {
        def cache = cacheManager.getCache('playerID-LHC')
        assert cache.get(player1.id).get() == player1
        cache = cacheManager.getCache('playerMD5-LHC')
        assert cache.get(player1.md5).get() == player1
        cache = cacheManager.getCache('playerSSID-LHC')
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1

        playerRepository.deleteAll()

        cache = cacheManager.getCache('playerID-LHC')
        assert cache.get(player1.id) == null
        cache = cacheManager.getCache('playerMD5-LHC')
        assert cache.get(player2.md5) == null
        cache = cacheManager.getCache('playerSSID-LHC')
        assert cache.get(player1.source + "/" + player1.sourceId) == null
    }

    @Test
    void testCacheDeleteSingly() {
        def cache = cacheManager.getCache('playerID-LHC')
        assert cache.get(player1.id).get() == player1
        cache = cacheManager.getCache('playerMD5-LHC')
        assert cache.get(player2.md5).get() == player2
        cache = cacheManager.getCache('playerSSID-LHC')
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1

        playerRepository.delete(player1.id)
        playerRepository.delete(player2)

        cache = cacheManager.getCache('playerID-LHC')
        assert cache.get(player1.id) == null
        assert cache.get(player3.id).get() == player3
        cache = cacheManager.getCache('playerMD5-LHC')
        assert cache.get(player2.md5) == null
        assert cache.get(player3.md5).get() == player3
        cache = cacheManager.getCache('playerSSID-LHC')
        assert cache.get(player1.source + "/" + player1.sourceId) == null
        assert cache.get(player3.source + "/" + player3.sourceId).get() == player3
    }

    @Test
    void testCacheDeleteMulti() {
        def cache = cacheManager.getCache('playerID-LHC')
        assert cache.get(player1.id).get() == player1
        cache = cacheManager.getCache('playerMD5-LHC')
        assert cache.get(player2.md5).get() == player2
        cache = cacheManager.getCache('playerSSID-LHC')
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1

        playerRepository.delete([player2, player1] as List<Player>)

        cache = cacheManager.getCache('playerID-LHC')
        assert cache.get(player1.id) == null
        assert cache.get(player3.id).get() == player3
        cache = cacheManager.getCache('playerMD5-LHC')
        assert cache.get(player2.md5) == null
        assert cache.get(player3.md5).get() == player3
        cache = cacheManager.getCache('playerSSID-LHC')
        assert cache.get(player1.source + "/" + player1.sourceId) == null
        assert cache.get(player3.source + "/" + player3.sourceId).get() == player3
    }

    @Test
    void testFindCaching() {
        //  Delete data underneath the cache to show still being pulled from caches
        MongoOperations operations = context.getBean(MongoOperations)
        operations.remove(Query.query(Criteria.where('source').is(player1.source)), 'player')

        assert playerRepository.findOne(player1.id) == player1
        assert playerRepository.findBySourceAndSourceId(player2.source, player2.sourceId) == player2
        assert playerRepository.findByMd5In([player3.md5, player4.md5]) as Set == [player3, player4] as Set
        assert playerRepository.findBySourceAndSourceIdIn(player2.source, [player2.sourceId, player1.sourceId]) as Set == [player2, player1] as Set

        //  Not cached
        assert playerRepository.findBySourceAndDisabled(player2.source, false).isEmpty()

        def cache = cacheManager.getCache('playerID-LHC')
        cache.clear()
        assert playerRepository.findOne(player1.id) == null
        cache = cacheManager.getCache('playerSSID-LHC')
        cache.clear()
        assert playerRepository.findBySourceAndSourceId(player2.source, player2.sourceId) == null
        assert playerRepository.findBySourceAndSourceIdIn(player2.source, [player2.sourceId, player1.sourceId]).isEmpty()
        cache = cacheManager.getCache('playerMD5-LHC')
        cache.clear()
        assert playerRepository.findByMd5In([player3.md5, player4.md5]).isEmpty()
    }
}


