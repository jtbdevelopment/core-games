package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.security.spring.social.facebook.FacebookProperties
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Date: 1/8/15
 * Time: 9:49 PM
 */
class SocialServiceTest extends GroovyTestCase {
    private SocialService service = new SocialService()

    void testClassAnnotations() {
        assert SocialService.class.isAnnotationPresent(Path.class)
        assert SocialService.class.getAnnotation(Path.class).value() == "social"
    }

    void testFacebookPropertiesAnnotations() {
        Field f = SocialService.class.getDeclaredField('facebookProperties')
        assert f
        assert f.isAnnotationPresent(Autowired.class)
        assertFalse f.getAnnotation(Autowired.class).required()
    }

    void testApiInfoAnnotations() {
        Method m = SocialService.class.getMethod('apiInfo')
        assert m
        assert m.isAnnotationPresent(GET.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert m.getAnnotation(Path.class).value() == 'apis'
    }

    void testRestWithNullProperties() {
        service.facebookProperties = null
        assert [:] == service.apiInfo()
    }

    void testRestWithWarningProperties() {
        service.facebookProperties = new FacebookProperties()
        service.facebookProperties.testDefaults()
        assert [:] == service.apiInfo()
    }

    void testRestWithGoodProperties() {
        service.facebookProperties = new FacebookProperties()
        service.facebookProperties.clientID = 'ID'
        service.facebookProperties.clientSecret = 'PROPS'
        service.facebookProperties.testDefaults()
        assert ['facebookAppId': 'ID'] == service.apiInfo()
    }
}
