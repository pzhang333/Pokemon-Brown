package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.util.Identifiable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jetty.util.ConcurrentHashSet;

public class Chunk extends Identifiable {

  public enum CHUNK_TYPE {
    DEFAULT,
    HEAL
  }

  private final int width;

  private final int height;

  private static int NEXT_DYNAMIC_ID = 0;

  private final boolean[][] grass;
  private List<Entity> entities = new ArrayList<>();
  private Set<Integer> usersHere = new ConcurrentHashSet<>();
  private final String fileName;
  private CHUNK_TYPE type = CHUNK_TYPE.DEFAULT;

  public Chunk(int id, int width, int height, String fileName) {
    super(id);
    this.width = width;
    this.height = height;
    this.grass = new boolean[this.width][this.height];
    this.fileName = fileName;
  }

  public void setType(CHUNK_TYPE type) {
    this.type = type;
  }

  public CHUNK_TYPE getType() {
    return this.type;
  }

  public String getFilename() {
    return this.fileName;
  }

  public boolean isGrass(int row, int col) {
    return this.grass[row][col];
  }

  public void setGrass(int row, int col, boolean grass) {
    this.grass[row][col] = grass;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public List<Entity> getEntities() {
    return this.getEntities(null);
  }

  public List<Entity> getEntities(User u) {
    return new ArrayList<>(this.entities);
  }

  public void addEntity(Entity e) {
    assert e.getLocation().getChunk() == this;
    this.entities.add(e);
  }

  public void removeEntity(Entity e) {
    this.entities.remove(e);
  }

  public void addUser(User u) {
    this.usersHere.add(u.getId());
    if (this.type == CHUNK_TYPE.HEAL) {
      u.getTeam().forEach(p -> {
        p.fullRestore();
      });
      for (Pokemon p : u.getTeam()) {
        for (Move m : p.getMoves()) {
          m.setPP(m.getPP());
        }
      }
    }
  }

  public void removeUser(User u) {
    this.usersHere.remove(u.getId());
  }

  public Collection<User> getUsers() {
    Set<User> users = new HashSet<>();
    for (int i : this.usersHere) {
      User u = UserManager.getUserById(i);
      if (u != null && u.isConnected()) {
        users.add(u);
      }
    }
    return Collections.unmodifiableSet(users);
  }

  public static int getNextDynamicId() {
    NEXT_DYNAMIC_ID--;
    return NEXT_DYNAMIC_ID;
  }

}
