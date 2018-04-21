package cs.brown.edu.aelp.networking;

/**
 * NetworkLocation.java.
 *
 * @author abrevnov17
 */
public class NetworkLocation {

  private int row;
  private int column;
  private int chunkId;

  public NetworkLocation(int chunkId, int row, int column) {
    this.setChunkId(chunkId);
    this.setRow(row);
    this.setColumn(column);
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public int getColumn() {
    return column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public int getChunkId() {
    return chunkId;
  }

  public void setChunkId(int chunkId) {
    this.chunkId = chunkId;
  }
}
