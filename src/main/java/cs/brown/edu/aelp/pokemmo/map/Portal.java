package cs.brown.edu.aelp.pokemmo.map;

public class Portal extends Entity {

  private final Location goTo;

  public Portal(Location loc, Location goTo) {
    super(loc);
    this.goTo = goTo;
  }

  public Location getGoTo() {
    return this.goTo;
  }

}
