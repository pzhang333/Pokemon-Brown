package cs.brown.edu.aelp.pokemmo.data.authentication;

import cs.brown.edu.aelp.networking.NetworkLocation;
import cs.brown.edu.aelp.networking.NetworkUser;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import java.util.HashMap;
import java.util.Map;

public class User {

  private final int id;
  private final String username;
  private final String email;
  private final String sessionToken;
  private final Map<Integer, Pokemon> pokemon = new HashMap<>();
  private Location location;
  private int currency = 0;
  private int state;
  private int orientation;

  public User(int id, String username, String email, String sessionToken) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.sessionToken = sessionToken;
  }

  public NetworkUser toNetworkUser() {
    return new NetworkUser(id, new NetworkLocation(location.getChunk().getId(), 
        location.getRow(), location.getCol()), state, orientation);
  }

  public void setState(int i) {
    this.state = i;
  }

  public int getState(int i) {
    return this.state;
  }

  public void setLocation(Location loc) {
    this.location = loc;
  }

  public Location getLocation() {
    return this.location;
  }

  public void setCurrency(int c) {
    this.currency = c;
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

  public void addPokemon(Pokemon p) {
    this.pokemon.put(p.getId(), p);
  }

  public Pokemon getPokemonById(int id) {
    assert this.pokemon.containsKey(id);
    return this.pokemon.get(id);
  }

  public int getOrientation() {
    return orientation;
  }

  public void setOrientation(int orientation) {
    this.orientation = orientation;
  }

}
