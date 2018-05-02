package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.util.JsonFile;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class World {

  private static final int BUSH_ID = 2804;
  private static final String CHUNKS_PATH = "src/main/resources/static/assets/maps/";
  private static final String CHUNK_FILE = "config/chunks.json";

  private Location spawn;
  private Map<Integer, Chunk> chunks = new ConcurrentHashMap<>();
  private Tournament tourney;

  public void addChunk(Chunk c) {
    chunks.put(c.getId(), c);
  }

  public Chunk getChunk(int id) {
    assert chunks.containsKey(id);
    return chunks.get(id);
  }

  public Tournament getTournament() {
    return this.tourney;
  }

  public void setTournament(Tournament t) {
    this.tourney = t;
  }

  public void setSpawn(Location loc) {
    this.spawn = loc;
  }

  public Location getSpawn() {
    assert this.spawn != null;
    return this.spawn;
  }

  public Collection<Chunk> getAllChunks() {
    return Collections.unmodifiableCollection(chunks.values());
  }

  public void loadChunks() {
    loadChunks(CHUNK_FILE);
  }

  public void removeChunk(Chunk c) {
    this.chunks.remove(c.getId());
  }

  public Chunk loadChunk(Integer id, String filename) throws IOException {
    File file = new File(CHUNKS_PATH + filename);
    String path = file.getAbsolutePath();
    JsonFile jFile = new JsonFile(path);
    String fname = file.getName();
    Chunk chunk = new Chunk(id, jFile.getInt("width"), jFile.getInt("height"),
        fname.substring(0, fname.lastIndexOf(".")));

    List<JsonFile> layers = jFile.getJsonList("layers");
    for (int i = 0; i < layers.size(); i++) {
      List<Double> tiles = layers.get(i).getList("data");
      if (tiles == null) {
        continue;
      }
      // String n = layers.get(i).getString("name");
      for (int j = 0; j < tiles.size(); j++) {
        int row = j / chunk.getWidth();
        int col = j % chunk.getWidth();
        if (tiles.get(j) == BUSH_ID) {
          chunk.addEntity(new Bush(new Location(chunk, row, col)));
        }
      }
    }
    this.addChunk(chunk);
    return chunk;
  }

  public void loadChunks(String path) {
    try {
      JsonFile f = new JsonFile(path);
      for (JsonFile chunk : f.getJsonList("chunks")) {
        int id = chunk.getInt("id");
        loadChunk(chunk.getInt("id"), chunk.getString("file"));
        System.out.printf("Loaded chunk %d from `%s`\n", id,
            chunk.getString("file"));
      }
      for (JsonFile chunk : f.getJsonList("chunks")) {
        List<JsonFile> portals = chunk.getJsonList("portals");
        for (JsonFile p : portals) {
          JsonFile start = p.getMap("location");
          JsonFile end = p.getMap("goto");
          Location loc = new Location(this.getChunk(chunk.getInt("id")),
              start.getInt("row"), start.getInt("col"));
          Location goTo = new Location(this.getChunk(end.getInt("chunk")),
              end.getInt("row"), end.getInt("col"));
          Portal portal = new Portal(loc, goTo);
          loc.getChunk().addEntity(portal);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
