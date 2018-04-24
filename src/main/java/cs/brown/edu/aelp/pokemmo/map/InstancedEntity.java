package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;

public abstract class InstancedEntity extends Entity {

  private final User instancedTo;

  public InstancedEntity(Location loc, User u) {
    super(loc);
    this.instancedTo = u;
  }

  public User getUser() {
    return this.instancedTo;
  }

}
