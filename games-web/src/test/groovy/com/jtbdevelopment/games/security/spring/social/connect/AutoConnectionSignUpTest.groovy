package com.jtbdevelopment.games.security.spring.social.connect

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.AbstractPlayer
import com.jtbdevelopment.games.players.PlayerFactory
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionKey
import org.springframework.social.connect.UserProfile
import org.springframework.social.connect.UserProfileBuilder

/**
 * Date: 1/7/15
 * Time: 7:04 AM
 */
class AutoConnectionSignUpTest extends GroovyTestCase {
    AutoConnectionSignUp autoConnectionSignUp = new AutoConnectionSignUp()

    private static class StringPlayer extends AbstractPlayer<String> {
        String id
        String md5

        @Override
        protected void setMd5(final String md5) {
            this.md5 = md5
        }

        @Override
        protected String getMd5Internal() {
            return md5
        }

        @Override
        String getIdAsString() {
            return id
        }
    }

    void testFindsPlayerInRepository() {
        String pid = 'APLAYER'
        String source = 'FBTLI'
        String sourceId = 'XXAAMM'
        autoConnectionSignUp.playerRepository = [
                findBySourceAndSourceId: {
                    String s, String sid ->
                        assert source == s
                        assert sourceId == sid
                        return new StringPlayer(id: pid)
                }
        ] as AbstractPlayerRepository
        Connection connection = [
                getKey: {
                    return new ConnectionKey(source, sourceId)
                }
        ] as Connection
        assert pid == autoConnectionSignUp.execute(connection)
    }

    void testSuccessfullyCreatesPlayer() {
        String pid = 'APLAYER'
        String source = 'FBTLI'
        String sourceId = 'XXAAMM'
        String imageUrl = 'http://image'
        String profileUrl = 'http://profile'
        String displayName = 'displayName'
        boolean saveCalled = false
        autoConnectionSignUp.playerRepository = [
                findBySourceAndSourceId: {
                    String s, String sid ->
                        assert source == s
                        assert sourceId == sid
                        return null
                },
                save                   : {
                    StringPlayer p ->
                        assert p.displayName == displayName
                        assertFalse p.disabled
                        assert p.profileUrl == profileUrl
                        assert p.imageUrl == imageUrl
                        assert p.sourceId == sourceId
                        assert p.source == source
                        assertNull p.id
                        saveCalled = true
                        p.id = pid
                        p
                }
        ] as AbstractPlayerRepository
        Connection connection = [
                getKey          : {
                    return new ConnectionKey(source, sourceId)
                },
                getProfileUrl   : {
                    return profileUrl
                },
                getImageUrl     : {
                    return imageUrl
                },
                fetchUserProfile: {
                    return new UserProfileBuilder().setName(displayName).build()
                }
        ] as Connection
        autoConnectionSignUp.playerFactory = [
                newPlayer: {
                    return new StringPlayer()
                }
        ] as PlayerFactory
        assert pid == autoConnectionSignUp.execute(connection)
        assert saveCalled
    }

    void testDAOFailsToCreatesPlayerSilently() {
        String source = 'FBTLI'
        String sourceId = 'XXAAMM'
        String imageUrl = 'http://image'
        String profileUrl = 'http://profile'
        String displayName = 'displayName'
        boolean saveCalled = false
        autoConnectionSignUp.playerRepository = [
                findBySourceAndSourceId: {
                    String s, String sid ->
                        assert source == s
                        assert sourceId == sid
                        return null
                },
                save                   : {
                    StringPlayer p ->
                        assert p.displayName == displayName
                        assertFalse p.disabled
                        assert p.profileUrl == profileUrl
                        assert p.imageUrl == imageUrl
                        assert p.sourceId == sourceId
                        assert p.source == source
                        assertNull p.id
                        saveCalled = true
                        null
                }
        ] as AbstractPlayerRepository
        Connection connection = [
                getKey          : {
                    return new ConnectionKey(source, sourceId)
                },
                getProfileUrl   : {
                    return profileUrl
                },
                getImageUrl     : {
                    return imageUrl
                },
                fetchUserProfile: {
                    return new UserProfileBuilder().setName(displayName).build()
                }
        ] as Connection
        autoConnectionSignUp.playerFactory = [
                newPlayer: {
                    return new StringPlayer()
                }
        ] as PlayerFactory
        assert null == autoConnectionSignUp.execute(connection)
        assert saveCalled
    }

    void testDAOFailsToCreatesPlayerViaException() {
        String source = 'FBTLI'
        String sourceId = 'XXAAMM'
        String imageUrl = 'http://image'
        String profileUrl = 'http://profile'
        String displayName = 'displayName'
        boolean saveCalled = false
        autoConnectionSignUp.playerRepository = [
                findBySourceAndSourceId: {
                    String s, String sid ->
                        assert source == s
                        assert sourceId == sid
                        return null
                },
                save                   : {
                    StringPlayer p ->
                        assert p.displayName == displayName
                        assertFalse p.disabled
                        assert p.profileUrl == profileUrl
                        assert p.imageUrl == imageUrl
                        assert p.sourceId == sourceId
                        assert p.source == source
                        assertNull p.id
                        saveCalled = true
                        throw new Exception('Some exception!')
                }
        ] as AbstractPlayerRepository
        Connection connection = [
                getKey          : {
                    return new ConnectionKey(source, sourceId)
                },
                getProfileUrl   : {
                    return profileUrl
                },
                getImageUrl     : {
                    return imageUrl
                },
                fetchUserProfile: {
                    return new UserProfileBuilder().setName(displayName).build()
                }
        ] as Connection
        autoConnectionSignUp.playerFactory = [
                newPlayer: {
                    return new StringPlayer()
                }
        ] as PlayerFactory
        assert null == autoConnectionSignUp.execute(connection)
        assert saveCalled
    }
}
