package cs.brown.edu.aelp.pokemmo.map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

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

  public double dist(Location other) {
    if (this.getChunk().getId() != other.getChunk().getId()) {
      return -1;
    }
    return Math.sqrt(Math.pow(this.getRow() - other.getRow(), 2)
        + Math.pow(this.getCol() - other.getCol(), 2));
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

  public JsonObject toJson() {
    JsonObject o = new JsonObject();
    o.addProperty("row", this.getRow());
    o.addProperty("col", this.getCol());
    o.addProperty("chunkId", this.getChunk().getId());
    return o;
  }

  public static class LocationAdapter implements JsonSerializer<Location> {

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc,
        JsonSerializationContext ctx) {
      JsonObject o = new JsonObject();
      o.addProperty("row", src.getRow());
      o.addProperty("col", src.getCol());
      o.addProperty("chunk_file", src.getChunk().getFilename());
      return o;
    }

  }

}
