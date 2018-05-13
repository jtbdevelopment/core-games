package com.jtbdevelopment.games.players;

import java.io.Serializable;

/**
 * Date: 12/30/2014 Time: 1:50 PM
 */
public interface SystemPlayer<ID extends Serializable> extends Player<ID> {

  String SYSTEM_SOURCE = "SOURCE";
}
