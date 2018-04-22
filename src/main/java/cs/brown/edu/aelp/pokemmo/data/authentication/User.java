package cs.brown.edu.aelp.pokemmo.data.authentication;

import cs.brown.edu.aelp.networking.NetworkUser;
import cs.brown.edu.aelp.pokemmo.data.BatchSavable;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Path;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.websocket.api.Session;

public class User extends Trainer implements BatchSavable {

  private final String username;
  private final String email;
  private String sessionToken;
  private Session session;

  private Map<String, Object> changes = new HashMap<>();

  private Path currentPath;
  private Location location;
  private int currency = 0;
  private int state;
  private int orientation;

  private final Map<Integer, Pokemon> pokemon = new HashMap<>();

  private final NetworkUser nUser;

  public User(int id, String username, String email, String sessionToken) {
    super(id);
    this.username = username;
    this.email = email;
    this.sessionToken = sessionToken;
    this.nUser = new NetworkUser(id);
  }

  public NetworkUser toNetworkUser() {
    return this.nUser;
  }

  public void setState(int i) {
    this.state = i;
    this.nUser.setPlayerState(i);
  }

  public int getState(int i) {
    return this.state;
  }

  public void setLocation(Location loc) {
    if (this.location.getChunk() != loc.getChunk()) {
      this.location.getChunk().removeUser(this);
      loc.getChunk().addUser(this);
    }
    this.location = loc;
    this.nUser.setLocation(loc.toNetworkLocation());
    this.addChange("chunk", loc.getChunk().getId());
    this.addChange("row", loc.getRow());
    this.addChange("col", loc.getCol());
  }

  public Location getLocation() {
    if (this.currentPath == null) {
      return this.location;
    } else {
      return this.currentPath.getCurrentStep();
    }
  }

  public void setCurrency(int c) {
    this.currency = c;
    this.addChange("currency", c);
  }

  public int getCurrency() {
    return this.currency;
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
    this.nUser.setOrientation(orientation);
  }

  public boolean isConnected() {
    return this.session != null && this.session.isOpen();
  }

  public void setPath(Path p) {
    this.currentPath = p;
    this.setLocation(p.getStart());
    this.nUser.setWalkingTo(p.getEnd().toNetworkLocation());
  }

  public Path getPath() {
    return this.currentPath;
  }

  public void setToken(String t) {
    this.sessionToken = t;
  }

  public void setSession(Session s) {
    this.session = s;
  }

  public Session getSession() {
    return this.session;
  }

  private void addChange(String key, Object o) {
    synchronized (this.changes) {
      this.changes.put(key, o);
    }
  }

  @Override
  public Map<String, Object> getChangesForSaving() {
    synchronized (this.changes) {
      Map<String, Object> toSave = new HashMap<>(this.changes);
      this.changes.clear();
      return toSave;
    }
  }

  public Collection<Pokemon> getAllPokemon() {
    return Collections.unmodifiableCollection(this.pokemon.values());
  }

}
