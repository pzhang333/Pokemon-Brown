package cs.brown.edu.aelp.networking;

import cs.brown.edu.aelp.Player.Player;
import cs.brown.edu.aelp.general_datastructures.Coordinate3d;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class GamePacket {
  
  private Coordinate3d position;
  private Collection<Player> otherPlayers;
  private Map<Coordinate3d, Integer> background;
  private Integer userState;

  public GamePacket(Coordinate3d playerPosition, 
      Collection<Player> otherPlayers, 
      int userState, 
      Map<Coordinate3d, Integer> backgroundMap) {
    
    this.position = playerPosition;
    this.background = backgroundMap;
    this.setOtherPlayers(localize(otherPlayers));
    this.userState = userState;
    
  }
  
  // Helper functions:
  
  private Collection<Player> localize(Collection<Player> players) {
    return players.stream().filter(p -> p.getPosition().getZ() 
        == this.position.getZ())
        .collect(Collectors.toList());
  }

  // Getters and Setters for Package Properties:
  
  public Coordinate3d getPosition() {
    return position;
  }

  public void setPosition(Coordinate3d position) {
    this.position = position;
  }

  public Map<Coordinate3d, Integer> getBackground() {
    return background;
  }

  public void setBackground(Map<Coordinate3d, Integer> background) {
    this.background = background;
  }

  public Integer getUserState() {
    return userState;
  }

  public void setUserState(Integer userState) {
    this.userState = userState;
  }

  public Collection<Player> getOtherPlayers() {
    return otherPlayers;
  }

  public void setOtherPlayers(Collection<Player> otherPlayers) {
    this.otherPlayers = otherPlayers;
  }

}
