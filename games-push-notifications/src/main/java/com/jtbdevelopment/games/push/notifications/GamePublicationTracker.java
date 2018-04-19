package com.jtbdevelopment.games.push.notifications;

import java.io.Serializable;

/**
 * Date: 10/10/2015 Time: 4:24 PM
 */
public class GamePublicationTracker implements Serializable {

  private Serializable pid;
  private Serializable gid;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!getClass().equals(o.getClass())) {
      return false;
    }

    GamePublicationTracker tracker = (GamePublicationTracker) o;

    return gid.equals(tracker.gid) && pid.equals(tracker.pid);
  }

  public int hashCode() {
    int result;
    result = pid.hashCode();
    result = 31 * result + gid.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "GamePublicationTracker{" + "pid=" + pid + ", gid=" + gid + "}";
  }

  public Serializable getPid() {
    return pid;
  }

  public void setPid(Serializable pid) {
    this.pid = pid;
  }

  public Serializable getGid() {
    return gid;
  }

  public void setGid(Serializable gid) {
    this.gid = gid;
  }
}
