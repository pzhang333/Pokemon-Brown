package cs.brown.edu.aelp.pokemon.data.authentication;

import cs.brown.edu.aelp.map.Chunk;
import cs.brown.edu.aelp.map.Tile;

public class User {

  private final int id;
  private final String username;
  private final String email;
  private final String sessionToken;
  private Chunk chunk;
  private Tile tile;
  private int currency;

  public User(int id, String username, String email, String sessionToken) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.sessionToken = sessionToken;
  }

  public void setChunk(Chunk c) {
    this.chunk = c;
  }

  public void setTile(Tile t) {
    this.tile = t;
  }

  public void setCurrency(int c) {
    this.currency = c;
  }

  public Chunk getChunk() {
    return this.chunk;
  }

  public Tile getTile() {
    return this.tile;
  }

  public int getCurrency() {
    return this.currency;
  }

  public int getId() {
    return this.id;
  }

  public String getUsername() {
    return this.username;
  }

  public String getEmail() {
    return this.email;
  }

  public String getToken() {
    return this.sessionToken;
  }

}
