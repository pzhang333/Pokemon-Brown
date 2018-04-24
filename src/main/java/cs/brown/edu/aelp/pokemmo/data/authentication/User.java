package cs.brown.edu.aelp.pokemmo.data.authentication;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.networking.PacketSender;
import cs.brown.edu.aelp.pokemmo.data.SQLBatchSavable;
import cs.brown.edu.aelp.pokemmo.map.Bush;
import cs.brown.edu.aelp.pokemmo.map.Entity;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Path;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.pokemon.Main;
import java.lang.reflect.Type;
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

  private Map<Location, Bush> expectedEncounters = new HashMap<>();
  private Path currentPath;
  private Location location;
  private int currency = 0;
  private int state;
  private int orientation;

  private final Map<Integer, Pokemon> pokemon = new HashMap<>();

  public User(int id, String username, String email, String sessionToken) {
    super(id);
    this.username = username;
    this.email = email;
    this.sessionToken = sessionToken;
  }

  public void setState(int i) {
    this.state = i;
  }

  public int getState() {
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

    if (this.getPath() != null) {
      for (Entity e : this.getPath().getExpectedEncounters()) {
        if (e.getLocation() == this.location) {
          this.interact(e);
        }
      }
    }

    this.setChanged(true);
  }

  public Location getLocation() {
    if (this.currentPath != null) {
      this.setLocation(this.currentPath.getCurrentStep());
      if (this.location.equals(this.currentPath.getEnd())) {
        this.currentPath = null;
      }
    }
    return this.location;
  }

  public void interact(Entity e) {
    if (e instanceof Bush) {
      Bush b = (Bush) e;
      Pokemon p = b.triggerEntry(this);
      if (p != null) {
        PacketSender.sendEncounterPacket(this, p);
      }
    }
  }

  public void setCurrency(int c) {
    this.currency = c;
    this.setChanged(true);
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
  }

  public boolean isConnected() {
    return this.session != null && this.session.isOpen();
  }

  public void setPath(Path p) {
    this.currentPath = p;
    this.setLocation(p.getStart());
  }

  public Path getPath() {
    return this.currentPath;
  }

  public void setToken(String t) {
    this.sessionToken = t;
  }

  public void setSession(Session s) {
    if (this.session != null && this.session.isOpen()) {
      this.session.close();
    }
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
    p.setInt(6, this.getId());
  }

  @Override
  public String getTableName() {
    return "users";
  }

  @Override
  public boolean hasUpdates() {
    return this.changed;
  }

  @Override
  public List<String> getIdentifyingColumns() {
    return Lists.newArrayList("id");
  }

  @Override
  public void setChanged(boolean b) {
    this.changed = b;
  }

  public static class UserAdapter implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User src, Type typeOfSrc,
        JsonSerializationContext ctx) {
      JsonObject o = new JsonObject();
      o.addProperty("id", src.getId());
      o.addProperty("state", src.getState());
      o.addProperty("orientation", src.getOrientation());
      o.add("location", Main.GSON().toJsonTree(src.getLocation()));
      if (src.getPath() != null) {
        o.add("destination", Main.GSON().toJsonTree(src.getPath().getEnd()));
      }
      return o;
    }

  }
}
