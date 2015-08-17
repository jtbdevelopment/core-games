package com.jtbdevelopment.games.players

import groovy.transform.CompileStatic
import org.springframework.data.annotation.Transient

import java.time.ZonedDateTime

/**
 * Date: 12/30/2014
 * Time: 11:08 AM
 */
@CompileStatic
interface Player<ID extends Serializable> {
    public <T extends GameSpecificPlayerAttributes> T getGameSpecificPlayerAttributes()

    public <T extends GameSpecificPlayerAttributes> void setGameSpecificPlayerAttributes(
            final T gameSpecificPlayerAttributes)

    ID getId()

    void setId(final ID id)

    @Transient
    String getIdAsString()

    String getSource()

    void setSource(final String source)

    String getSourceId()

    void setSourceId(final String sourceId)

    String getDisplayName()

    void setDisplayName(final String displayName)

    String getImageUrl()

    void setImageUrl(String imageUrl)

    String getProfileUrl()

    void setProfileUrl(String profileUrl)

    boolean getDisabled()

    boolean isDisabled()

    void setDisabled(boolean disabled)

    PlayerPayLevel getPayLevel()

    void setPayLevel(final PlayerPayLevel payLevel)

    boolean getAdminUser()

    boolean isAdminUser()

    void setAdminUser(boolean adminUser)

    String getMd5()

    String getSourceAndSourceId()

    ZonedDateTime getCreated()

    ZonedDateTime getLastLogin()

    void setLastLogin(final ZonedDateTime lastLogin)

    String getLastVersionNotes()

    void setLastVersionNotes(final String lastVersionNotes)
}