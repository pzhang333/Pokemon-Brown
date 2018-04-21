package cs.brown.edu.aelp.pokemmo.map;

import java.util.HashMap;
import java.util.Map;

public class World {

  private Map<Integer, Chunk> chunks = new HashMap<>();

  public void addChunk(Chunk c) {
    chunks.put(c.getId(), c);
  }

  public Chunk getChunk(int id) {
    assert chunks.containsKey(id);
    return chunks.get(id);
  }

}
