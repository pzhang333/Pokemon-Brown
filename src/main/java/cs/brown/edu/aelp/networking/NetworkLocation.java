package cs.brown.edu.aelp.networking;

/**
 * NetworkLocation.java.
 *
 * @author abrevnov17
 */
public class NetworkLocation {

  private int row;
  private int column;
  private String chunkId;

  public NetworkLocation(String chunkId, int row, int column) {
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

  public String getChunkId() {
    return chunkId;
  }

  public void setChunkId(String chunkId) {
    this.chunkId = chunkId;
  }
}
