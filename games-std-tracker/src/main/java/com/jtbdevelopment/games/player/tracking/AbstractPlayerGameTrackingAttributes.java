package com.jtbdevelopment.games.player.tracking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes;
import com.jtbdevelopment.games.players.Player;
import org.springframework.data.annotation.Transient;

/**
 * Date: 1/30/15 Time: 6:34 PM
 */
public abstract class AbstractPlayerGameTrackingAttributes implements GameSpecificPlayerAttributes {

  public static final String FREE_GAMES_FIELD = "gameSpecificPlayerAttributes.freeGamesUsedToday";
  static final String PAID_GAMES_FIELD = "gameSpecificPlayerAttributes.availablePurchasedGames";
  private int freeGamesUsedToday = 0;
  private int availablePurchasedGames = 0;
  @Transient
  private Player player;

  public abstract int getMaxDailyFreeGames();

  @SuppressWarnings("unused")
  public abstract void setMaxDailyFreeGames(int maxDailyFreeGames);

  @SuppressWarnings("WeakerAccess")
  public int getFreeGamesUsedToday() {
    return freeGamesUsedToday;
  }

  @SuppressWarnings("WeakerAccess")
  public void setFreeGamesUsedToday(int freeGamesUsedToday) {
    this.freeGamesUsedToday = freeGamesUsedToday;
  }

  @SuppressWarnings("WeakerAccess")
  public int getAvailablePurchasedGames() {
    return availablePurchasedGames;
  }

  @SuppressWarnings("WeakerAccess")
  public void setAvailablePurchasedGames(int availablePurchasedGames) {
    this.availablePurchasedGames = availablePurchasedGames;
  }

  @Transient
  @JsonIgnore
  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }
}
