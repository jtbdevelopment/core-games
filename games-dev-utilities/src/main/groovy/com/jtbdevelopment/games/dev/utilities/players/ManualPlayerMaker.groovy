package com.jtbdevelopment.games.dev.utilities.players

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.PlayerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Date: 11/22/2014
 * Time: 1:48 PM
 */
class ManualPlayerMaker {
    static PasswordEncoder passwordEncoder;

    static PlayerFactory playerFactory

    public static void main(final String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext("com.jtbdevelopment");

        AbstractPlayerRepository repository = ctx.getBean(AbstractPlayerRepository.class)
        playerFactory = ctx.getBean(PlayerFactory.class)
        passwordEncoder = ctx.getBean(PasswordEncoder.class)

        ManualPlayer[] players = [
                makePlayer("Manual Player1", 'M1@MANUAL.COM', "M1"),
                makePlayer("Manual Player2", 'M2@MANUAL.COM', "M2"),
                makePlayer("Manual Player3", 'M3@MANUAL.COM', "M3"),
                makePlayer("Manual Player4", 'M4@MANUAL.COM', "M4"),
                makePlayer("Manual Player5", 'M5@MANUAL.COM', "M5"),
                makePlayer("Manual Player6", 'M6@MANUAL.COM', "M6"),
        ]

        players.each {
            ManualPlayer it ->
                ManualPlayer loaded = (ManualPlayer) repository.findBySourceAndSourceId(it.source, it.sourceId);
                if (!loaded) {
                    println "Creating player " + it
                    repository.save(it)
                } else {
                    println "Player already created " + it
                }
        }

        println "Complete"
    }

    static ManualPlayer makePlayer(final String displayName, final String sourceId, final String password) {
        ManualPlayer manualPlayer = (ManualPlayer) playerFactory.newManualPlayer()
        manualPlayer.disabled = false
        manualPlayer.adminUser = true
        manualPlayer.verified = true
        manualPlayer.displayName = displayName
        manualPlayer.sourceId = sourceId
        manualPlayer.password = passwordEncoder.encode(password)
        return manualPlayer
    }
}
