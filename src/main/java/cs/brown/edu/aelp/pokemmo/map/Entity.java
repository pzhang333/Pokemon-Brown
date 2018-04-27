package cs.brown.edu.aelp.pokemmo.map;

public abstract class Entity {

  private Location loc;

  // This class should be the superclass of anything that goes dynamically on
  // the map, e.g. NPC, portals, tables

  public Entity(Location loc) {
    this.loc = loc;
  }

  public Location getLocation() {
    return this.loc;
  }

  public void setLocation(Location loc) {
    if (this.loc.getChunk() != loc.getChunk()) {
      this.loc.getChunk().removeEntity(this);
      this.loc = loc;
      loc.getChunk().addEntity(this);
    }
    this.loc = loc;
  }

}
