package cs.brown.edu.aelp.map;

public class Location {

  private final Chunk chunk;
  private final int row;
  private final int col;

  public Location(Chunk c, int row, int col) {
    this.chunk = c;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((chunk == null) ? 0 : chunk.hashCode());
    result = prime * result + col;
    result = prime * result + row;
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
    Location other = (Location) obj;
    if (chunk == null) {
      if (other.chunk != null)
        return false;
    } else if (!chunk.equals(other.chunk))
      return false;
    if (col != other.col)
      return false;
    if (row != other.row)
      return false;
    return true;
  }

}
