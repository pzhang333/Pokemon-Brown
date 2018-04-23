package cs.brown.edu.aelp.networking;

import cs.brown.edu.aelp.util.Identifiable;

public class NetworkUser extends Identifiable {

  private NetworkLocation location;
  private NetworkLocation walkingTo;
  private int playerState;
  private int orientation;

  public NetworkUser(int id) {
    super(id);
    this.setPlayerState(0);
    this.orientation = 0;
    this.setLocation(new NetworkLocation(-1, -1, -1));
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

  public NetworkLocation getLocation() {
    return location;
  }

  public void setLocation(NetworkLocation location) {
    this.location = location;
  }

  public void setWalkingTo(NetworkLocation location) {
    this.walkingTo = location;
  }
  
  public NetworkLocation getWalkingTo() {
    return this.walkingTo;
  }
}
