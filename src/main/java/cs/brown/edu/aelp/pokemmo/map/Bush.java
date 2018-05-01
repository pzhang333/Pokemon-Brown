package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bush extends Entity {

  private final static double chance = 0.50;
  private final static long cooldown = 30 * 1000;
  private final static Random r = new Random();
  Map<User, Long> cooldowns = new HashMap<>();

  public Bush(Location loc) {
    super(loc);
  }

  public boolean triggerEntry(User u) {
    if (cooldowns.containsKey(u)) {
      if (cooldowns.get(u) > System.currentTimeMillis()) {
        return false;
      } else {
        cooldowns.remove(u);
      }
    }
    cooldowns.put(u, System.currentTimeMillis() + cooldown);
    double d = r.nextDouble();
    return d < chance;
  }

}
