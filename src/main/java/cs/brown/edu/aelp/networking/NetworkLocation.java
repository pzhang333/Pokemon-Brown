package cs.brown.edu.aelp.networking;

import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemon.Main;

/**
 * NetworkLocation.java.
 *
 * @author abrevnov17
 */
public class NetworkLocation {

  private final int row;
  private final int column;
  private final int chunkId;

  public NetworkLocation(int chunkId, int row, int column) {
    this.chunkId = chunkId;
    this.row = row;
    this.column = column;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public int getChunkId() {
    return chunkId;
  }

  public Location toLocation() {
    return new Location(Main.getWorld().getChunk(this.chunkId), this.row, this.column);
  }
}
