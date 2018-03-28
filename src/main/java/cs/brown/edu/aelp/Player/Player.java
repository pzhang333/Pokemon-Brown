package cs.brown.edu.aelp.Player;

import cs.brown.edu.aelp.general_datastructures.Coordinate3d;

public class Player {

  private String uuid;
  private Coordinate3d position;
  private int userState;
  
  public Player(String uuid) {
    this.setUuid(uuid);
    this.setPosition(null);
    this.setUserState(0);
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

  public int getUserState() {
    return userState;
  }

  public void setUserState(int userState) {
    this.userState = userState;
  }

}
