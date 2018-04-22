package com.jtbdevelopment.games.push.notifications


/**
 * Date: 10/30/15
 * Time: 6:26 PM
 */
class GamePublicationTrackerTest extends GroovyTestCase {
    GamePublicationTracker t1 = new GamePublicationTracker(gid: "GID1", pid: "PID1")
    GamePublicationTracker t2 = new GamePublicationTracker(gid: "GID1", pid: "PID1")
    GamePublicationTracker t3 = new GamePublicationTracker(gid: "GID2", pid: "PID1")
    GamePublicationTracker t4 = new GamePublicationTracker(gid: "GID1", pid: "PID2")

    void testEquals() {
        assert t1.equals(t2)
        assertFalse t1.equals(t3)
        assertFalse t2.equals(t3)
        assertFalse t1.equals(t4)
        assertFalse t2.equals(t4)
    }

    void testHashCode() {
        assert t1.hashCode() == t2.hashCode()
        assert t1.hashCode() != t3.hashCode()
        assert t1.hashCode() != t4.hashCode()
        assert t2.hashCode() != t3.hashCode()
        assert t2.hashCode() != t4.hashCode()
    }
}
