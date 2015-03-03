package com.jtbdevelopment.games.players

import groovy.transform.CompileStatic
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.util.StringUtils

/**
 * Date: 11/3/14
 * Time: 6:53 AM
 */
@CompileStatic
abstract class AbstractPlayer<ID extends Serializable> implements Cloneable, Player<ID>, Serializable {

    String source
    String sourceId
    String displayName
    String imageUrl
    String profileUrl
    boolean disabled = false
    boolean adminUser = false
    PlayerPayLevel payLevel = PlayerPayLevel.FreeToPlay
    GameSpecificPlayerAttributes gameSpecificPlayerAttributes

    boolean equals(final o) {
        if (this.is(o)) return true
        if (!(o instanceof AbstractPlayer)) return false

        final AbstractPlayer player = (AbstractPlayer) o

        if (id != player.id) return false

        return true
    }

    int hashCode() {
        return idAsString ? idAsString.hashCode() : 0
    }

    void setSource(final String source) {
        if (StringUtils.isEmpty(this.source)) {
            this.source = source
            computeMD5Hex()
        }
    }

    void setSourceId(final String sourceId) {
        this.sourceId = sourceId
        computeMD5Hex()
    }

    void setDisplayName(final String displayName) {
        this.displayName = displayName
        computeMD5Hex()
    }

    abstract protected void setMd5(final String md5);

    abstract protected String getMd5Internal()

    String getMd5() {
        if (StringUtils.isEmpty(getMd5Internal())) {
            computeMD5Hex()
        }
        return this.getMd5Internal()
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", disabled=" + disabled +
                '}';
    }

    protected void computeMD5Hex() {
        String md5
        if (idAsString == null || source == null || displayName == null || sourceId == null) {
            md5 = ""
        } else {
            String key = idAsString + source + displayName + sourceId
            md5 = DigestUtils.md5Hex(key)
        }
        setMd5(md5)
    }
}
