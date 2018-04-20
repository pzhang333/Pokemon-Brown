package cs.brown.edu.aelp.pokemon.data.authentication;

import cs.brown.edu.aelp.map.Location;
import cs.brown.edu.aelp.pokemon.Pokemon;
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

  public User(int id, String username, String email, String sessionToken) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.sessionToken = sessionToken;
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

}
