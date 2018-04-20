package cs.brown.edu.aelp.networking;

import cs.brown.edu.aelp.Player.Player;
import cs.brown.edu.aelp.general_datastructures.Coordinate3d;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class GamePacket {
  
  private Collection<Player> otherPlayers;
  private Player player;

  public GamePacket(Player player, 
      Collection<Player> otherPlayers, Map<Coordinate3d, Integer> backgroundMap) {
    
    this.setOtherPlayers(localize(player.getPosition(), otherPlayers));
    this.setPlayer(player);
  }
  
  // Helper functions:
  
  private Collection<Player> localize(Coordinate3d playerPosition, Collection<Player> players) {
    return players.stream().filter(p -> p.getPosition().getZ() 
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
