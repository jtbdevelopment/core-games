package com.jtbdevelopment.games.dev.utilities.players;

import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnection;
import com.jtbdevelopment.core.spring.social.dao.AbstractSocialConnectionRepository;
import com.jtbdevelopment.core.spring.social.dao.SocialConnection;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import groovy.transform.CompileStatic;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Date: 12/4/2016
 * Time: 12:51 PM
 */
@CompileStatic
public class CleanUpDeadSocialConnections {

    public static void main(final String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext("com.jtbdevelopment");

        AbstractSocialConnectionRepository socialRepository = ctx
            .getBean(AbstractSocialConnectionRepository.class);
        final AbstractPlayerRepository playerRepository = ctx
            .getBean(AbstractPlayerRepository.class);

        Iterable<AbstractSocialConnection> all = socialRepository.findAll();
        List<SocialConnection> deletable = StreamSupport.stream(all.spliterator(), false)
            .filter(sc -> !playerRepository.findById(sc.getUserId()).isPresent())
            .collect(Collectors.toList());

        socialRepository.delete(deletable);
    }

}
