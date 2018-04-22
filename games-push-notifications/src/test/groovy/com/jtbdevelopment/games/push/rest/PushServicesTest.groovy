package com.jtbdevelopment.games.push.rest

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.players.notifications.RegisteredDevice
import com.jtbdevelopment.games.push.PushProperties
import com.jtbdevelopment.games.rest.services.SecurityService
import com.jtbdevelopment.games.security.SessionUserInfo
import com.jtbdevelopment.games.stringimpl.StringPlayer
import groovy.transform.TypeChecked
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

import javax.annotation.security.RolesAllowed
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import java.lang.reflect.Method

/**
 * Date: 10/16/15
 * Time: 7:05 PM
 */
class PushServicesTest extends GameCoreTestCase {
    PushServices services = new PushServices()

    void testClassAnnotations() {
        assert SecurityService.class.isAnnotationPresent(RolesAllowed.class)
        assert SecurityService.class.getAnnotation(RolesAllowed.class).value() == [PlayerRoles.PLAYER]
        assert SecurityService.class.isAnnotationPresent(Path.class)
        assert SecurityService.class.getAnnotation(Path.class).value() == "security"
    }

    void testSenderIDAnnotations() {
        Method m = PushServices.class.getMethod('senderID')
        assert m
        assert m.isAnnotationPresent(GET.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.TEXT_PLAIN]
        assert m.getAnnotation(Path.class).value() == 'senderID'

    }

    void testSenderID() {
        def SENDERID = "SENDERID"
        services.pushProperties = new PushProperties(senderID: SENDERID)
        assert SENDERID == services.senderID()
    }

    void testAddingDeviceAnnotations() {
        Method m = PushServices.class.getMethod("registerDevice", [String.class] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(PUT.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert m.isAnnotationPresent(Path.class)
        assert m.getAnnotation(Path.class).value() == "register/{deviceID}"
        def params = m.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "deviceID"
    }

    void testAddingDevice() {
        SecurityContextHolder.context = new SecurityContextImpl()
        def session = new SessionUserInfo<String>() {
            Player<String> sessionUser = PONE
            Player<String> effectiveUser = PONE

            @Override
            Player<String> getSessionUser() {
                return sessionUser
            }

            @Override
            Player<String> getEffectiveUser() {
                return effectiveUser
            }

            @Override
            void setEffectiveUser(final Player<String> player) {
                this.effectiveUser = player
            }
        }
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(session, null)
        SecurityContextHolder.context.authentication = authenticationToken

        RegisteredDevice device = new RegisteredDevice(deviceID: "some id over here")
        Player saved = new StringPlayer()
        def repo = [
                findById: {
                    String id ->
                        assert id == PONE.id
                        return Optional.of(PONE)
                },
                save    : {
                    Player p ->
                        assert p.registeredDevices.contains(device)
                        return saved
                }
        ] as AbstractPlayerRepository

        services.playerRepository = repo
        assert saved.is(services.registerDevice(device.deviceID))
    }

    void testRemoveDeviceAnnotations() {
        Method m = PushServices.class.getMethod("unregisteredDevice", [String.class] as Class<?>[])
        assert (m.annotations.size() == 3 ||
                (m.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && m.annotations.size() == 4)
        )
        assert m.isAnnotationPresent(PUT.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert m.isAnnotationPresent(Path.class)
        assert m.getAnnotation(Path.class).value() == "unregister/{deviceID}"
        def params = m.parameterAnnotations
        assert params.length == 1
        assert params[0].length == 1
        assert params[0][0].annotationType() == PathParam.class
        assert ((PathParam) params[0][0]).value() == "deviceID"
    }

    void testRemovedDevice() {
        SecurityContextHolder.context = new SecurityContextImpl()
        def session = new SessionUserInfo<String>() {
            Player<String> sessionUser = PONE
            Player<String> effectiveUser = PONE

            @Override
            Player<String> getSessionUser() {
                return sessionUser
            }

            @Override
            Player<String> getEffectiveUser() {
                return effectiveUser
            }

            @Override
            void setEffectiveUser(final Player<String> player) {
                this.effectiveUser = player
            }
        }
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(session, null)
        SecurityContextHolder.context.authentication = authenticationToken


        RegisteredDevice device = new RegisteredDevice(deviceID: "some id over here")
        PONE.updateRegisteredDevice(device)
        Player saved = new StringPlayer()
        def repo = [
                findById: {
                    String id ->
                        assert id == PONE.id
                        return Optional.of(PONE)
                },
                save    : {
                    Player p ->
                        assert p.registeredDevices.empty
                        return saved
                }
        ] as AbstractPlayerRepository

        services.playerRepository = repo
        assert saved.is(services.unregisteredDevice(device.deviceID))
    }
}
