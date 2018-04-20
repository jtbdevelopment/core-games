package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.masking.MaskedGame;

/**
 * Date: 12/8/14 Time: 6:59 AM
 */
public class WebSocketMessage {

  private MessageType messageType;
  private MaskedGame game;
  private Player player;
  private String message;

  public MessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  public MaskedGame getGame() {
    return game;
  }

  public void setGame(MaskedGame game) {
    this.game = game;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public enum MessageType {
    Heartbeat, Game, Player, Alert;
  }
}
