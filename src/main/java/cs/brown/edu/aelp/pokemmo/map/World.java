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
  private static final String DEFAULT_CHUNK_PATH = "src/main/resources/static/assets/maps";

  private Location spawn;
  private Map<Integer, Chunk> chunks = new ConcurrentHashMap<>();

  private String prefix = "chunk_";
  private String suffix = ".json";

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

  public Collection<Chunk> getAllChunks() {
    return Collections.unmodifiableCollection(chunks.values());
  }

  public void loadChunks() {
    loadChunks(DEFAULT_CHUNK_PATH);
  }

  public Chunk loadChunk(Integer id, File file) throws IOException {

    String path = file.getAbsolutePath();

    JsonFile jFile = new JsonFile(path);

    Chunk chunk = new Chunk(id, jFile.getInt("width"), jFile.getInt("height"),
        false);

    // 2804 in layer "Bush" = Bush
    List<JsonFile> layers = jFile.getJsonList("layers");
    for (int i = 0; i < layers.size(); i++) {
      List<Integer> tiles = layers.get(i).getList("data");
      String n = layers.get(i).getString("name");
      System.out.println(n);
      for (int j = 0; j < tiles.size(); j++) {
        int row = j / chunk.getWidth();
        int col = j % chunk.getWidth();
        if (n.equals("Bush")) {
          System.out.println(tiles.get(j));
        }
        if (n.equals("Bush") && tiles.get(j) == BUSH_ID) {
          chunk.addEntity(new Bush(new Location(chunk, row, col)));
          System.out.printf("Placing bush at: (%d, %d) in chunk %d%n", row, col,
              chunk.getId());
        }
      }
    }
    return chunk;
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

            Chunk chunk = loadChunk(chunkId, file);
            System.out.printf("Loaded %s%d from `%s`\n", prefix, chunkId,
                file.getAbsolutePath());

            addChunk(chunk);

          } catch (NumberFormatException ex) {
            System.err.printf("Warning: invalid chunk id `%s`\n", chunkName);
          } catch (IOException ex) {
            System.err.printf("Error: failed to load chunk `%s`\n", chunkName);
          }
        }

      }
    }

  }
}
