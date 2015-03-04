package com.jtbdevelopment.games.publish.cluster

import groovy.transform.CompileStatic

/**
 * Date: 3/3/15
 * Time: 6:57 PM
 */
@CompileStatic
class ClusterMessage {
    public enum ClusterMessageType {
        GameUpdate,         //  gameId and playerId
        PlayerUpdate,       //  playerId only
        ClearPlayerCache    //  no content
    }
    ClusterMessageType clusterMessageType
    String gameId
    String playerId
}
