package cs.brown.edu.aelp.pokemmo.map;

import java.util.HashMap;
import java.util.Map;

public class World {

  private Location spawn;
  private Map<Integer, Chunk> chunks = new HashMap<>();

  public void addChunk(Chunk c) {
    chunks.put(c.getId(), c);
  }

  public Chunk getChunk(int id) {
    assert chunks.containsKey(id);
    return chunks.get(id);
  }

  public void setSpawn(Location loc) {
    this.spawn = loc;
  }

  public Location getSpawn() {
    assert this.spawn != null;
    return this.spawn;
  }

}
