package cs.brown.edu.aelp.networking;

import cs.brown.edu.aelp.Player.Player;
import cs.brown.edu.aelp.general_datastructures.Coordinate3d;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GamePacket {
  
  private Coordinate3d position;
  private Map<Player, Coordinate3d> otherPlayersPositionsMap;
  private Map<Coordinate3d, Integer> background;
  private Integer userState;

  public GamePacket(Coordinate3d playerPosition, 
      Map<Player, Coordinate3d> positionPlayerMap, 
      int userState, 
      Map<Coordinate3d, Integer> backgroundMap) {
    
    this.position = playerPosition;
    this.background = backgroundMap;
    this.otherPlayersPositionsMap = 
        new HashMap<Player, Coordinate3d>(localize(positionPlayerMap));
    this.userState = userState;
    
  }
  
  // Helper functions:
  
  private Map<Player, Coordinate3d> localize(Map<Player, Coordinate3d> wholeMap) {
    return wholeMap.entrySet().stream()
        .filter(map -> map.getValue().getZ() == position.getZ())
        .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
  }

  // Getters and Setters for Package Properties:
  
  public Coordinate3d getPosition() {
    return position;
  }

  public void setPosition(Coordinate3d position) {
    this.position = position;
  }

  public Map<Player, Coordinate3d> getOtherPlayersPositionsMap() {
    return otherPlayersPositionsMap;
  }

  public void setOtherPlayersPositionsMap(Map<Player, Coordinate3d> otherPlayersPositionsMap) {
    this.otherPlayersPositionsMap = otherPlayersPositionsMap;
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

}
