package cs.brown.edu.aelp.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chunk {

  private final String id;
  private final int width;
  private final int height;
  private Map<Location, List<Entity>> entities = new HashMap<>();

  public Chunk(String id, int width, int height) {
    this.id = id;
    this.width = width;
    this.height = height;
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

  public List<Entity> getEntitiesAt(Location loc) {
    if (this.entities.containsKey(loc)) {
      return new ArrayList<>(this.entities.get(loc));
    }
    return new ArrayList<>();
  }

  public void setEntitiesAt(Location loc, List<Entity> entities) {
    this.entities.put(loc, entities);
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
