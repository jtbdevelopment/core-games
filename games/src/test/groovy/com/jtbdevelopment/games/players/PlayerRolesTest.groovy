package com.jtbdevelopment.games.players
/**
 * Date: 12/22/14
 * Time: 12:19 PM
 */
class PlayerRolesTest extends GroovyTestCase {
    void testPlayer() {
        assert PlayerRoles.PLAYER == 'ROLE_Player'
    }

    void testAdmin() {
        assert PlayerRoles.ADMIN == 'ROLE_Admin'
    }
}
