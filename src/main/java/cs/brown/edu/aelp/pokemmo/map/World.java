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
  public static final String DEFAULT_CHUNK_PATH = "src/main/resources/static/assets/maps";

  private Location spawn;
  private Map<Integer, Chunk> chunks = new ConcurrentHashMap<>();
  private Tournament tourney;

  private String prefix = "chunk_";
  private String suffix = ".json";

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
    loadChunks(DEFAULT_CHUNK_PATH);
  }

  public void removeChunk(Chunk c) {
    this.chunks.remove(c.getId());
  }

  public Chunk loadChunk(Integer id, File file) throws IOException {

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

  public void loadPortals(String path) throws IOException {
    JsonFile f = new JsonFile(path + "/portals.json");
    List<JsonFile> portals = f.getJsonList("portals");
    for (JsonFile p : portals) {
      JsonFile start = p.getMap("location");
      JsonFile end = p.getMap("goto");
      Location loc = new Location(this.getChunk(start.getInt("chunkId")),
          start.getInt("row"), start.getInt("col"));
      Location goTo = new Location(this.getChunk(end.getInt("chunkId")),
          end.getInt("row"), end.getInt("col"));
      Portal portal = new Portal(loc, goTo);
      loc.getChunk().addEntity(portal);
    }
  }

  public void loadChunks(String path) {

    File[] files = new File(path).listFiles();

    if (files == null) {
      return;
    }

    for (File file : files) {
      if (file.isFile()) {

        String fileName = file.getName();

        if (fileName.startsWith(prefix) && fileName.endsWith(suffix)) {

          String chunkName = fileName.substring(prefix.length(),
              fileName.length() - suffix.length());

          try {

            Integer chunkId = Integer.parseInt(chunkName);

            loadChunk(chunkId, file);
            System.out.printf("Loaded %s%d from `%s`\n", prefix, chunkId,
                file.getAbsolutePath());

          } catch (NumberFormatException ex) {
            System.err.printf("Warning: invalid chunk id `%s`\n", chunkName);
          } catch (IOException ex) {
            System.err.printf("Error: failed to load chunk `%s`\n", chunkName);
          }
        }

      }
    }
    try {
      loadPortals(path);
    } catch (IOException e) {
      System.err.println("ERROR: Failed to load portals.");
    }

  }
}
