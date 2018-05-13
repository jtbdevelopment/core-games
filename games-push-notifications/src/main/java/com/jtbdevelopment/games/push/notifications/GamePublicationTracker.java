package com.jtbdevelopment.games.push.notifications;

import java.io.Serializable;

/**
 * Date: 10/10/2015 Time: 4:24 PM
 */
public class GamePublicationTracker<T extends Serializable> implements Serializable {

  private T pid;
  private T gid;

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

  @SuppressWarnings("WeakerAccess")
  public T getPid() {
    return pid;
  }

  public void setPid(final T pid) {
    this.pid = pid;
  }

  @SuppressWarnings("WeakerAccess")
  public T getGid() {
    return gid;
  }

  public void setGid(final T gid) {
    this.gid = gid;
  }
}
