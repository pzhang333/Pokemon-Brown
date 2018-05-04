package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import java.util.Random;

public class Bush extends Entity {

  private final static double chance = 0.50;
  private final static Random r = new Random();

  public Bush(Location loc) {
    super(loc);
  }

  public boolean triggerEntry(User u) {
    double d = r.nextDouble();
    return d < chance;
  }

  @Override
  public int getCooldown() {
    return 30 * 1000;
  }

}
