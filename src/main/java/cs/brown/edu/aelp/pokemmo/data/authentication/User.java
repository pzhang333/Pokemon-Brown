package cs.brown.edu.aelp.pokemmo.data.authentication;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.networking.PacketSender;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler.OP_CODES;
import cs.brown.edu.aelp.pokemmo.data.SQLBatchSavable;
import cs.brown.edu.aelp.pokemmo.map.Bush;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.map.Entity;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Path;
import cs.brown.edu.aelp.pokemmo.map.Portal;
import cs.brown.edu.aelp.pokemmo.map.Tournament;
import cs.brown.edu.aelp.pokemmo.map.World;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.pokemon.Inventory;
import cs.brown.edu.aelp.pokemon.Main;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.jetty.websocket.api.Session;

public class User extends Trainer implements SQLBatchSavable {

  private final String username;
  private final String email;
  private String sessionToken;
  private Session session;

  private boolean changed = false;

  private Inventory inventory;
  private Path currentPath;
  private Location location;
  private int currency = 0;
  private int state;
  private int orientation;
  private int elo = 100;

  public User(int id, String username, String email, String sessionToken) {
    super(id);
    this.username = username;
    this.email = email;
    this.sessionToken = sessionToken;
    this.inventory = new Inventory(this);
  }

  public Inventory getInventory() {
    return this.inventory;
  }

  public void setState(int i) {
    this.state = i;
  }

  public int getState() {
    return this.state;
  }

  public void setElo(int elo) {
    this.elo = elo;
  }

  public int getElo() {
    return this.elo;
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
        if (e.getLocation().equals(this.location)) {
          this.interact(e);
        }
      }
    }

    if (this.currentPath != null
        && this.location.equals(this.currentPath.getEnd())) {
      this.currentPath = null;
    }

    this.setChanged(true);
  }

  public Location getLocation() {
    if (this.currentPath != null) {
      this.setLocation(this.currentPath.getCurrentStep());
    }
    return this.location;
  }

  public void interact(Entity e) {
    if (e instanceof Bush) {
      Bush b = (Bush) e;
      Pokemon p = b.triggerEntry(this);
      if (p != null) {
        System.out.printf("User %d found a pokemon in the bushes.%n",
            this.getId());
        PacketSender.sendEncounterPacket(this);
      }
    } else if (e instanceof Portal) {
      Portal p = (Portal) e;
      World w = Main.getWorld();
      if (w.getTournament() != null) {
        Tournament t = w.getTournament();
        if (t.getEntrance().equals(p)) {
          if (!t.canJoin(this)) {
            this.sendMessage(t.whyCantJoin(this));
            return;
          } else {
            t.addUser(this);
          }
        } else if (t.getExit().equals(p)) {
          t.removeUser(this);
        }
      }
      this.teleportTo(p.getGoTo());
    }
  }

  public void teleportTo(Location l) {
    this.setPath(null);
    Chunk old = this.getLocation().getChunk();
    this.setLocation(l);
    PacketSender.sendInitializationPacket(this);
    JsonObject leftChunkOp = PacketSender.buildPlayerOpMessage(this,
        OP_CODES.LEFT_CHUNK);
    PacketSender.queueOpForChunk(leftChunkOp, old);
    JsonObject enteredChunkOp = PacketSender.buildPlayerOpMessage(this,
        OP_CODES.ENTERED_CHUNK);
    PacketSender.queueOpForChunk(enteredChunkOp, l.getChunk());
  }

  public void sendMessage(String s) {
    PacketSender.sendServerMessagePacket(this, s);
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
    if (p != null) {
      this.setLocation(p.getStart());
    }
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

  public List<Pokemon> getAllPokemon() {
    return getTeam();
  }

  public int updateElo(boolean won, int otherElo) {
    int K = 32;
    int s = won ? 1 : 0;
    double r1 = Math.pow(10, (this.elo / 400));
    double r2 = Math.pow(10, (otherElo / 400));
    double e1 = r1 / (r1 + r2);
    r1 = r1 + (K * (s - e1));
    this.setElo((int) r1);
    return (int) r1;
  }

  public void validateLocation() {
    if (this.getLocation().getChunk() == null
        || this.getLocation().getChunk().getId() < 0) {
      this.setLocation(new Location(Main.getWorld().getChunk(1), 5, 5));
    }
  }

  @Override
  public List<String> getUpdatableColumns() {
    return Lists.newArrayList("chunk", "row", "col", "currency",
        "session_token");
  }

  @Override
  public void bindValues(PreparedStatement p) throws SQLException {
    Location l = this.getLocation();
    if (l.getChunk().getId() < 0) {
      // we never want to save people into a dynamic chunk that may not exist in
      // the future
      p.setInt(1, 1);
      p.setInt(2, 1);
      p.setInt(3, 1);
    } else {
      p.setInt(1, l.getChunk().getId());
      p.setInt(2, l.getRow());
      p.setInt(3, l.getCol());
    }
    p.setInt(4, this.getCurrency());
    p.setString(5, this.getToken());
    p.setInt(6, this.getId());
    p.addBatch();
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
      o.add("items", Main.GSON().toJsonTree(src.getInventory()));
      o.addProperty("elo", src.getElo());
      if (src.getPath() != null) {
        o.add("destination", Main.GSON().toJsonTree(src.getPath().getEnd()));
      }
      return o;
    }

  }
}
