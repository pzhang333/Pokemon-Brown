package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.util.JsonFile;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.postgresql.util.Base64;

public class World {

  private static final int GRASS = -6;
  private static final String DEFAULT_CHUNK_PATH = "src/main/resources/static/assets/maps";
  private final static Random r = new Random();

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

    // determine which tiles in this chunk are grassy

    List<JsonFile> layers = jFile.getObjectArray("layers");
    for (int i = 0; i < layers.size(); i++) {
      String n = layers.get(i).getString("name");
      if (n.equals("Base") || n.equals("Collision")) {
        String data = layers.get(i).getString("data");
        byte[] decoded = Base64.decode(data);
        int[] bytes = new int[decoded.length];
        for (int j = 0; j < decoded.length; j += 4) {
          bytes[j / 4] = (decoded[j] | decoded[j + 1] << 8
              | decoded[j + 2] << 16 | decoded[j + 3] << 24) >>> 0;
        }
        // for some reason we have 4x more ints than we need, but that's ok,
        // we'll just look at first quarter
        for (int j = 0; j < chunk.getHeight() * chunk.getWidth(); j++) {
          int row = j / chunk.getWidth();
          int col = j % chunk.getWidth();
          // this is a pretty hacky way to do this, but hopefully it's accurate
          if (n.equals("Base") && bytes[j] == GRASS) {
            chunk.setGrass(row, col, true);
          } else if (n.equals("Collision") && bytes[j] != 0) {
            chunk.setGrass(row, col, false);
          }
        }
      }
    }

    // generate some random bushes on the grass
    int tries = (int) (chunk.getWidth() * chunk.getHeight() * 0.002);
    while (tries > 0) {
      // pick a random spot
      int row = r.nextInt(chunk.getHeight());
      int col = r.nextInt(chunk.getWidth());
      // pick random dimensions
      int width = r.nextInt(8) + 1;
      int height = r.nextInt(8) + 1;
      int start_row = (int) (row - (height / 2));
      int start_col = (int) (col - (width / 2));
      int end_row = start_row + (2 * height);
      int end_col = start_col + (2 * width);
      for (int i = start_row; i < end_row; i++) {
        for (int j = start_col; j < end_col; j++) {
          if (i > 0 && i < chunk.getHeight() && j > 0 && j < chunk.getWidth()
              && chunk.isGrass(i, j)) {
            // randomly drop out 20%
            if (r.nextDouble() >= 0.2) {
              Bush bush = new Bush(new Location(chunk, i, j));
              chunk.addEntity(bush);
            }
          }
        }
      }
      tries--;
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
