package cs.brown.edu.aelp.Player;

import cs.brown.edu.aelp.general_datastructures.Coordinate3d;

public class Player {

  private String uuid;
  private Coordinate3d position;
  private int playerState;
  
  public Player(String uuid) {
    this.setUuid(uuid);
    this.setPosition(null);
    this.setPlayerState(0);
  }

  // Getters and Setters: 
  
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Coordinate3d getPosition() {
    return position;
  }

  public void setPosition(Coordinate3d position) {
    this.position = position;
  }

  public int getPlayerState() {
    return playerState;
  }

  public void setPlayerState(int playerState) {
    this.playerState = playerState;
  }

}
