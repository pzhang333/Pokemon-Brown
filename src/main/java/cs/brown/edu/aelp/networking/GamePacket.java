package cs.brown.edu.aelp.networking;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class GamePacket {

  private Collection<NetworkUser> otherPlayers;
  private NetworkUser player;

  public GamePacket(NetworkUser player, Collection<NetworkUser> otherPlayers) {
    if (player.getLocation().getChunkId() == -1) {
      this.setOtherPlayers(Collections.emptyList());
    } else {
      this.setOtherPlayers(localize(player, otherPlayers));
    }
    this.setPlayer(player);
  }

  // Helper functions:

  /**
   * Returns all of the players that are on the same chunk as a given player.
   * 
   * @param playerPosition
   *          Player position.
   * @param players
   *          Collection of all players.
   * @return Collection of players on the same chunk as the given player (based
   *         on position).
   */

  private Collection<NetworkUser> localize(NetworkUser player,
      Collection<NetworkUser> players) {
    return players.stream().filter(p -> (p.getLocation().getChunkId() != -1)
        && p.getLocation().getChunkId() == player.getLocation().getChunkId())
        .collect(Collectors.toList());
  }

  // Getters and Setters for Package Properties:

  public Collection<NetworkUser> getOtherPlayers() {
    return otherPlayers;
  }

  public void setOtherPlayers(Collection<NetworkUser> otherPlayers) {
    this.otherPlayers = otherPlayers;
  }

  public NetworkUser getPlayer() {
    return player;
  }

  public void setPlayer(NetworkUser player) {
    this.player = player;
  }

}
