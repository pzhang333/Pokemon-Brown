package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import java.util.HashMap;
import java.util.Map;

public abstract class Entity {

  private Location loc;
  private Map<User, Long> cooldowns = new HashMap<>();

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

  public void remove() {
    this.getLocation().getChunk().removeEntity(this);
  }

  public boolean canInteract(User u) {
    if (cooldowns.containsKey(u)) {
      if (cooldowns.get(u) > System.currentTimeMillis()) {
        return false;
      }
    }
    cooldowns.put(u, System.currentTimeMillis() + this.getCooldown());
    return true;
  }

  public int getCooldown() {
    return 1000;
  }

}
