package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.util.Identifiable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Chunk extends Identifiable {

  private final int width;

  private final int height;

  private static int NEXT_DYNAMIC_ID = 0;

  private final boolean[][] grass;
  private List<Entity> entities = new ArrayList<>();
  private Set<User> usersHere = new HashSet<>();
  private final String fileName;

  private final boolean instanced;

  public Chunk(int id, int width, int height, boolean instanced,
      String fileName) {
    super(id);
    this.width = width;
    this.height = height;
    this.instanced = instanced;
    this.grass = new boolean[this.width][this.height];
    this.fileName = fileName;
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

  public boolean isInstanced() {
    return this.instanced;
  }

  public List<Entity> getEntities() {
    return this.getEntities(null);
  }

  public List<Entity> getEntities(User u) {
    if (this.instanced) {
      List<Entity> results = new ArrayList<>();
      for (Entity e : this.entities) {
        if (e instanceof InstancedEntity) {
          InstancedEntity ie = (InstancedEntity) e;
          if (u != null && ie.getUser().equals(u)) {
            results.add(ie);
          }
        } else {
          results.add(e);
        }
      }
      return results;
    } else {
      return new ArrayList<>(this.entities);
    }
  }

  public void addEntity(Entity e) {
    assert e.getLocation().getChunk() == this;
    if (e instanceof InstancedEntity) {
      assert this.instanced;
    }
    this.entities.add(e);
  }

  public void removeEntity(Entity e) {
    this.entities.remove(e);
  }

  public void addUser(User u) {
    this.usersHere.add(u);
  }

  public void removeUser(User u) {
    this.usersHere.remove(u);
  }

  public Collection<User> getUsers() {
    return Collections.unmodifiableSet(this.usersHere.stream()
        .filter(User::isConnected).collect(Collectors.toSet()));
  }

  public static int getNextDynamicId() {
    NEXT_DYNAMIC_ID--;
    return NEXT_DYNAMIC_ID;
  }

}
