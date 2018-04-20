package cs.brown.edu.aelp.networking;

import cs.brown.edu.aelp.Player.Player;
import cs.brown.edu.aelp.general_datastructures.Coordinate3d;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class GamePacket {
  
  private Collection<Player> otherPlayers;
  private Player player;

  public GamePacket(Player player, 
      Collection<Player> otherPlayers) {
    if (player.getPosition().equals(new Coordinate3d(-1,-1,-1))) {
      this.setOtherPlayers(Collections.emptyList());
    } else {
      this.setOtherPlayers(localize(player.getPosition(), otherPlayers));
    }
    this.setPlayer(player);
  }
  
  // Helper functions:
  
  /**
   * Returns all of the players that are on the same chunk as a given player.
   * @param playerPosition
   *        Player position.
   * @param players
   *        Collection of all players.
   * @return Collection of players on the same chunk as the given player (based on position).
   */
  private Collection<Player> localize(Coordinate3d playerPosition, Collection<Player> players) {
    return players.stream().filter(p -> 
    p.getPosition() != new Coordinate3d(-1,-1,-1) && p.getPosition().getZ() 
        == playerPosition.getZ())
        .collect(Collectors.toList());
  }

  // Getters and Setters for Package Properties:

  public Collection<Player> getOtherPlayers() {
    return otherPlayers;
  }

  public void setOtherPlayers(Collection<Player> otherPlayers) {
    this.otherPlayers = otherPlayers;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

}
