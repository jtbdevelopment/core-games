package com.jtbdevelopment.games.players;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 12/22/14 Time: 12:19 PM
 */
public class PlayerRolesTest {

    @Test
    public void testPlayer() {
        Assert.assertEquals("ROLE_Player", PlayerRoles.PLAYER);
    }

    @Test
    public void testAdmin() {
        Assert.assertEquals("ROLE_Admin", PlayerRoles.ADMIN);
    }

}
