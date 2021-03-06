package cs.brown.edu.aelp.pokemmo.data.authentication;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cs.brown.edu.aelp.networking.Challenge;
import cs.brown.edu.aelp.networking.PacketSender;
import cs.brown.edu.aelp.networking.PlayerWebSocketHandler.OP_CODES;
import cs.brown.edu.aelp.networking.Trade;
import cs.brown.edu.aelp.networking.Trade.TRADE_STATUS;
import cs.brown.edu.aelp.pokemmo.data.SQLBatchSavable;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.map.Entity;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Path;
import cs.brown.edu.aelp.pokemmo.map.Tournament;
import cs.brown.edu.aelp.pokemmo.map.World;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.pokemon.Inventory;
import cs.brown.edu.aelp.pokemon.Main;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

  private Inventory inventory;
  private Path currentPath;
  private Location location;
  private int currency = 0;
  private int state;
  private int orientation;
  private int elo = 100;
  private Map<Integer, Pokemon> inactivePokemon = new HashMap<>();
  private Challenge pendingChallenge;
  private Trade activeTrade;

  public User(int id, String username, String email, String sessionToken) {
    super(id);
    this.username = username;
    this.email = email;
    this.sessionToken = sessionToken;
    this.inventory = new Inventory(this);
  }

  @Override
  public Pokemon getPokemonById(int id) {
    if (this.inactivePokemon.containsKey(id)) {
      return this.inactivePokemon.get(id);
    }
    return super.getPokemonById(id);
  }

  public boolean isBusy() {
    return this.isInBattle() || this.getChallenge() != null
        || this.getActiveTrade() != null;
  }

  public void setChallenge(Challenge c) {
    this.pendingChallenge = c;
  }

  public Trade getActiveTrade() {
    return this.activeTrade;
  }

  public void setActiveTrade(Trade t) {
    this.activeTrade = t;
  }

  public Challenge getChallenge() {
    return this.pendingChallenge;
  }

  public void addInactivePokemon(Pokemon p) {
    this.inactivePokemon.put(p.getId(), p);
  }

  public void removeInactivePokemon(Pokemon p) {
    this.inactivePokemon.remove(p.getId());
  }

  public void clearInactivePokemon() {
    this.inactivePokemon.clear();
  }

  public Collection<Pokemon> getInactivePokemon() {
    return Collections.unmodifiableCollection(this.inactivePokemon.values());
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
    this.changed = true;
  }

  public int getElo() {
    return this.elo;
  }

  public void setLocation(Location loc) {
    if (this.location == null || this.location.getChunk() != loc.getChunk()) {
      if (this.location != null) {
        this.location.getChunk().removeUser(this);
        this.sendMessage(
            "You have switched chunks and are now in a new chat channel.");
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
    if (!e.canInteract(this)) {
      return;
    }
    e.interact(this);
  }

  public void kick() {
    this.setToken(null);
    if (this.isConnected()) {
      this.getSession().close();
      this.setSession(null);
      this.disconnectCleanup();
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
    System.out.printf("Messaging %s: %s%n", this.getUsername(), s);
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

  @Override
  public void setActivePokemon(Pokemon p) {
    super.setActivePokemon(p);
    this.changed = true;
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
    List<Pokemon> pokes = new ArrayList<>(this.getTeam());
    pokes.addAll(this.getInactivePokemon());
    return pokes;
  }

  public int updateElo(boolean won, int otherElo) {
    int K = 32;
    int s = won ? 1 : 0;
    double R1 = Math.pow(10, (this.elo / 400));
    double R2 = Math.pow(10, (otherElo / 400));
    double e1 = R1 / (R1 + R2);
    int newElo = (int) (this.elo + (K * (s - e1)));
    this.setElo(newElo);
    return newElo;
  }

  public void validateLocation() {
    if (this.getLocation().getChunk() == null
        || this.getLocation().getChunk().getId() < 0) {
      this.setLocation(Main.getWorld().getSpawn());
    }
  }

  public void disconnectCleanup() {
    // remove user from any battles they may be in
    if (this.isInBattle()) {
      this.getCurrentBattle().forfeit(this);
    }
    if (this.getChallenge() != null) {
      PacketSender.sendChallengeResponse(this.getChallenge().other(this),
          "canceled");
      this.getChallenge().cancel();
    }
    if (this.getActiveTrade() != null) {
      Trade t = this.getActiveTrade();
      t.setStatus(TRADE_STATUS.CANCELED);
      PacketSender.sendTradePacket(t.other(this), t);
      t.other(this).setActiveTrade(null);
      this.setActiveTrade(null);
    }
    // remove them from the tournament
    World w = Main.getWorld();
    if (w.getTournament() != null) {
      Tournament t = w.getTournament();
      if (t.isParticipating(this)) {
        t.removeUser(this);
      }
    }
  }

  @Override
  public List<String> getUpdatableColumns() {
    return Lists.newArrayList("chunk", "row", "col", "currency",
        "session_token", "active_pokemon", "elo");
  }

  @Override
  public void bindValues(PreparedStatement p) throws SQLException {
    Location l = this.getLocation();
    if (l.getChunk().getId() < 0) {
      // we never want to save people into a dynamic chunk that may not exist in
      // the future
      Location spawn = Main.getWorld().getSpawn();
      p.setInt(1, spawn.getChunk().getId());
      p.setInt(2, spawn.getRow());
      p.setInt(3, spawn.getCol());
    } else {
      p.setInt(1, l.getChunk().getId());
      p.setInt(2, l.getRow());
      p.setInt(3, l.getCol());
    }
    p.setInt(4, this.getCurrency());
    p.setString(5, this.getToken());
    p.setInt(6, this.getActivePokemon().getId());
    p.setInt(7, this.getElo());
    p.setInt(8, this.getId());
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

  @Override
  public boolean useUpsert() {
    return false;
  }

  public static class UserAdapter implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User src, Type typeOfSrc,
        JsonSerializationContext ctx) {
      JsonObject o = new JsonObject();
      o.addProperty("id", src.getId());
      o.addProperty("username", src.getUsername());
      o.addProperty("state", src.getState());
      o.addProperty("orientation", src.getOrientation());
      o.add("location", Main.GSON().toJsonTree(src.getLocation()));
      o.add("items", Main.GSON().toJsonTree(src.getInventory()));
      o.add("pokemon", Main.GSON().toJsonTree(src.getAllPokemon()));
      o.addProperty("active_pokemon", src.getActivePokemon().getId());
      o.addProperty("currency", src.getCurrency());
      o.addProperty("elo", src.getElo());
      if (src.getPath() != null) {
        o.add("destination", Main.GSON().toJsonTree(src.getPath().getEnd()));
      }
      return o;
    }

  }

  public void addCaughtPokemon(Pokemon wild) {
    Pokemon p = Main.getDataSource().insertNewPokemon(this, wild,
        this.getTeam().size() >= 5);
    if (p != null) {
      if (p.isStored()) {
        this.addInactivePokemon(p);
      } else {
        this.addPokemonToTeam(p);
      }
    }
  }
}
