package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.masking.MaskedGame;

/**
 * Date: 12/8/14 Time: 6:59 AM
 */
@SuppressWarnings("WeakerAccess")
public class WebSocketMessage {

  private MessageType messageType;
  private MaskedGame game;
  private Player player;
  private String message;

  public WebSocketMessage() {

  }

  public WebSocketMessage(
      final MessageType messageType,
      final MaskedGame game,
      final Player player,
      final String message) {
    this.message = message;
    this.messageType = messageType;
    this.player = player;
    this.game = game;
  }

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

  public enum MessageType {
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
