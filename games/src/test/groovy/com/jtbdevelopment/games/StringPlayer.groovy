package com.jtbdevelopment.games

import com.jtbdevelopment.games.players.AbstractPlayer

/**
 * Date: 11/8/14
 * Time: 9:09 AM
 */
class StringPlayer extends AbstractPlayer<String> {
    private String md5
    String id

    void setId(final String id) {
        this.id = id
        computeMD5Hex()
    }

    @Override
    protected void setMd5(final String md5) {
        this.md5 = md5
    }

    @Override
    protected String getMd5Internal() {
        return this.md5
    }

    @Override
    String getIdAsString() {
        return id
    }
}

