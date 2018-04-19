package cs.brown.edu.aelp.map;

public class Tile {

  private final Chunk chunk;
  private final int row;
  private final int col;
  private TileType type = TileType.TRAVERSABLE;
  private Chunk portalTo;

  public Tile(Chunk chunk, int row, int col) {
    this.chunk = chunk;
    this.row = row;
    this.col = col;
  }

  public Chunk getChunk() {
    return this.chunk;
  }

  public int getRow() {
    return this.row;
  }

  public int getCol() {
    return this.col;
  }

  public TileType getType() {
    return this.type;
  }

  public void setPortal(Chunk chunk) {
    this.setType(TileType.PORTAL);
    this.portalTo = chunk;
  }

  public void setType(TileType type) {
    this.type = type;
  }

}
