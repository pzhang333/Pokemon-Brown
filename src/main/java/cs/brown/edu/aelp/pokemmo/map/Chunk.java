package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chunk {

  private final String id;
  private final int width;
  private final int height;
  private Map<Location, List<Entity>> entities = new HashMap<>();
  private Map<User, Map<Location, List<Entity>>> instanced_entities;
  private final boolean instanced;

  public Chunk(String id, int width, int height, boolean instanced) {
    this.id = id;
    this.width = width;
    this.height = height;
    this.instanced = instanced;
    if (instanced) {
      this.instanced_entities = new HashMap<>();
    }
  }

  public String getId() {
    return this.id;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Chunk other = (Chunk) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

}