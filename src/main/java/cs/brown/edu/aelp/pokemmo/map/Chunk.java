package cs.brown.edu.aelp.pokemmo.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.util.Identifiable;

public class Chunk extends Identifiable {

  private final int width;

  private final int height;

  private Map<Location, List<Entity>> entities = new ConcurrentHashMap<>();

  private Map<User, Map<Location, List<Entity>>> instanced_entities;

  private final boolean instanced;

  public Chunk(int id, int width, int height, boolean instanced) {
    super(id);
    this.width = width;
    this.height = height;
    this.instanced = instanced;
    if (instanced) {
      this.instanced_entities = new ConcurrentHashMap<>();
    }
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public List<Entity> getEntitiesAt(Location loc, User user) {
    List<Entity> results = new ArrayList<>();
    if (this.entities.containsKey(loc)) {
      results.addAll(this.entities.get(loc));
    }
    if (this.instanced && this.instanced_entities.containsKey(user)
        && this.instanced_entities.get(user).containsKey(loc)) {
      results.addAll(this.instanced_entities.get(user).get(loc));
    }
    return results;
  }

  public void addEntityAt(Entity e, Location loc) {
    if (!this.entities.containsKey(loc)) {
      this.entities.put(loc, new ArrayList<Entity>());
    }
    this.entities.get(loc).add(e);
  }

  public void addInstancedEntityAt(Entity e, Location loc, User user) {
    if (!this.instanced) {
      throw new IllegalArgumentException(
          "Only instanced chunks can have instanced entities.");
    }
    if (!this.instanced_entities.containsKey(user)) {
      this.instanced_entities.put(user, new HashMap<>());
    }
    Map<Location, List<Entity>> user_entities = this.instanced_entities
        .get(user);
    if (!user_entities.containsKey(loc)) {
      user_entities.put(loc, new ArrayList<>());
    }
    user_entities.get(loc).add(e);
  }

}
