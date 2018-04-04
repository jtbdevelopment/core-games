package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 11/4/2014
 * Time: 9:54 PM
 */
@CompileStatic
abstract class AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class)

    @Autowired
    AbstractPlayerRepository<? extends Serializable, ? extends Player<? extends Serializable>> playerRepository

    protected Player loadPlayerMD5(final String md5) {
        Player p = playerRepository.findByMd5(md5)
        if (p == null) {
            throw new FailedToFindPlayersException()
        }
        p
    }

    protected Set<Player> loadPlayerMD5s(final Collection<String> playerMD5s) {
        List<? extends Player<? extends Serializable>> loadedPlayers = playerRepository.findByMd5In(playerMD5s)
        LinkedHashSet<Player> players = new LinkedHashSet<>(loadedPlayers.collect { Player it -> it })
        if (players.size() != playerMD5s.size()) {
            logger.info("Not all players were loaded " + playerMD5s + " vs. " + players)
            throw new FailedToFindPlayersException()
        }
        players
    }

    protected Player loadPlayer(final Serializable playerID) {
        def optional = playerRepository.findById(playerID)
        if (optional.present) {
            return optional.get()
        }
        logger.info("Player was not loaded " + playerID.toString())
        throw new FailedToFindPlayersException()
    }

}
