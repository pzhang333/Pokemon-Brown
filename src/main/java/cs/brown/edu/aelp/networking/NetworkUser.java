package cs.brown.edu.aelp.networking;

public class NetworkUser {

  private int uid;
  private NetworkLocation location;
  private int playerState;
  private int orientation;
  
  public NetworkUser(int uid) {
    this.setUid(uid);
    this.setPlayerState(0);
    this.orientation = 0;
    this.setLocation(new NetworkLocation("", -1, -1));
  }
  
  public NetworkUser(int uid, NetworkLocation location, 
      int playerState, int orientation) {
    this.setUid(uid);
    this.setPlayerState(playerState);
    this.orientation = orientation;
    this.setLocation(location);
  }

  // Getters and Setters: 
  
  public int getUid() {
    return uid;
  }

  public void setUid(int uid) {
    this.uid = uid;
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

}
