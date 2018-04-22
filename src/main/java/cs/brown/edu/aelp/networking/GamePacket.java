package cs.brown.edu.aelp.networking;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class GamePacket {

  private Collection<NetworkUser> players;

  public GamePacket(int chunkId, Collection<NetworkUser> players) {
    this.setPlayers(localize(chunkId, players));
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

  private Collection<NetworkUser> localize(int chunkId,
      Collection<NetworkUser> players) {
    return players.stream()
        .filter(p -> (!(p.getLocation().getChunkId() == -1)) 
            && p.getLocation().getChunkId() == chunkId)
        .collect(Collectors.toList());
  }

  public Collection<NetworkUser> getPlayers() {
    return players;
  }

  public void setPlayers(Collection<NetworkUser> players) {
    this.players = players;
  }

  // Getters and Setters for Package Properties:


}
