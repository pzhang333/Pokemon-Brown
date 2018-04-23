package cs.brown.edu.aelp.pokemmo.data.authentication;

import com.google.common.collect.Lists;
import cs.brown.edu.aelp.networking.NetworkUser;
import cs.brown.edu.aelp.pokemmo.data.SQLBatchSavable;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Path;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.websocket.api.Session;

public class User extends Trainer implements SQLBatchSavable {

  private final String username;
  private final String email;
  private String sessionToken;
  private Session session;

  private boolean changed = false;

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
    if (this.location == null || this.location.getChunk() != loc.getChunk()) {
      if (this.location != null) {
        this.location.getChunk().removeUser(this);
      }
      loc.getChunk().addUser(this);
    }
    this.location = loc;
    this.nUser.setLocation(loc.toNetworkLocation());
    this.changed = true;
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
    this.changed = true;
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

  public Collection<Pokemon> getAllPokemon() {
    return this.pokemon.values();
  }

  @Override
  public List<String> getUpdatableColumns() {
    return Lists.newArrayList("chunk", "row", "col", "currency",
        "session_token");
  }

  @Override
  public void bindValues(PreparedStatement p) throws SQLException {
    Location l = this.getLocation();
    p.setInt(1, l.getChunk().getId());
    p.setInt(2, l.getRow());
    p.setInt(3, l.getCol());
    p.setInt(4, this.getCurrency());
    p.setString(5, this.getToken());
  }

  @Override
  public String getTableName() {
    return "users";
  }

  @Override
  public boolean hasUpdates() {
    return this.changed;
  }
}
