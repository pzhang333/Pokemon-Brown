package cs.brown.edu.aelp.map;

import java.util.HashMap;
import java.util.Map;

public class World {

  private Map<String, Chunk> chunks = new HashMap<>();

  public void addChunk(Chunk c) {
    chunks.put(c.getId(), c);
  }

  public Chunk getChunk(String id) {
    assert chunks.containsKey(id);
    return chunks.get(id);
  }

}
