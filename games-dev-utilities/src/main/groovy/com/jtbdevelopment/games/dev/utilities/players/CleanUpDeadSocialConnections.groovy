package com.jtbdevelopment.games.dev.utilities.players

import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnection
import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnectionRepository
import com.jtbdevelopment.core.spring.social.dao.SocialConnection
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Date: 12/4/2016
 * Time: 12:51 PM
 */

@CompileStatic
class CleanUpDeadSocialConnections {
    static void main(final String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext("com.jtbdevelopment")

        AbstractSocialConnectionRepository socialRepository = ctx.getBean(AbstractSocialConnectionRepository.class)
        AbstractPlayerRepository playerRepository = ctx.getBean(AbstractPlayerRepository.class)

        List<SocialConnection> deletable = []
        Iterable<AbstractSocialConnection> all = socialRepository.findAll()
        all.each {
            AbstractSocialConnection socialConnection ->
                def player = playerRepository.findById(new ObjectId(socialConnection.userId))
                if (!player.present) {
                    deletable.add(socialConnection)
                }
        }

        socialRepository.delete(deletable)
    }
}
