package com.jtbdevelopment.games.players

import groovy.transform.CompileStatic
import org.springframework.data.annotation.Transient

/**
 * Date: 12/30/2014
 * Time: 11:08 AM
 */
@CompileStatic
interface Player<ID extends Serializable> {
    ID getId();

    void setId(final ID id);

    @Transient
    String getIdAsString();

    String getSource();

    void setSource(final String source);

    String getSourceId();

    void setSourceId(final String sourceId);

    String getDisplayName();

    void setDisplayName(final String displayName);

    String getImageUrl();

    void setImageUrl(String imageUrl);

    String getProfileUrl();

    void setProfileUrl(String profileUrl);

    boolean getDisabled();

    boolean isDisabled();

    void setDisabled(boolean disabled);

    boolean getAdminUser();

    boolean isAdminUser();

    void setAdminUser(boolean adminUser);

    String getMd5();
}