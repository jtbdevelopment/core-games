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

  WebSocketMessage() {

  }

  WebSocketMessage(
      final MessageType messageType,
      final MaskedGame game,
      final Player player,
      final String message) {
    this.message = message;
    this.messageType = messageType;
    this.player = player;
    this.game = game;
  }

  MessageType getMessageType() {
    return messageType;
  }

  void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  MaskedGame getGame() {
    return game;
  }

  void setGame(MaskedGame game) {
    this.game = game;
  }

  Player getPlayer() {
    return player;
  }

  void setPlayer(Player player) {
    this.player = player;
  }

  String getMessage() {
    return message;
  }

  void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WebSocketMessage)) {
      return false;
    }

    WebSocketMessage that = (WebSocketMessage) o;

    return messageType == that.messageType &&
        (game != null ? game.equals(that.game) : that.game == null) &&
        (player != null ? player.equals(that.player) : that.player == null) &&
        (message != null ? message.equals(that.message) : that.message == null);
  }

  enum MessageType {
    Heartbeat, Game, Player
  }

  @Override
  public int hashCode() {
    int result = messageType.hashCode();
    result = 31 * result + (game != null ? game.hashCode() : 0);
    result = 31 * result + (player != null ? player.hashCode() : 0);
    result = 31 * result + (message != null ? message.hashCode() : 0);
    return result;
  }
}
