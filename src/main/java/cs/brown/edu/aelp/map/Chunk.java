package cs.brown.edu.aelp.map;

public class Chunk {

  private final String id;
  private final int width;
  private final int height;
  private Tile[][] tiles;

  public Chunk(String id, int width, int height) {
    this.id = id;
    this.width = width;
    this.height = height;
    tiles = new Tile[height][width];
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

  public Tile getTileAt(int row, int col) {
    assert row < this.height;
    assert col < this.width;
    // may be null
    return tiles[row][col];
  }

  public Tile createTile(TileType type, int row, int col) {
    Tile t = new Tile(this, row, col);
    t.setType(type);
    this.tiles[row][col] = t;
    // portal setting has to be done elsewhere,
    // probably after all chunks have been created
    return t;
  }
}
