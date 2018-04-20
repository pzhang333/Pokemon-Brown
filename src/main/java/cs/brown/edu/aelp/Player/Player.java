package cs.brown.edu.aelp.Player;

import cs.brown.edu.aelp.general_datastructures.Coordinate3d;

public class Player {

  private String uuid;
  private Coordinate3d position;
  private int playerState;
  private int orientation;
  
  public Player(String uuid) {
    this.setUuid(uuid);
    this.position = new Coordinate3d(-1,-1,-1);
    this.setPlayerState(0);
    this.orientation = 0;
  }
  
  public Player(String uuid, Coordinate3d position, int playerState, int orientation) {
    this.setUuid(uuid);
    this.setPosition(position);
    this.setPlayerState(orientation);
    this.orientation = orientation;
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

  public int getOrientation() {
    return orientation;
  }

  public void setOrientation(int orientation) {
    this.orientation = orientation;
  }

}
