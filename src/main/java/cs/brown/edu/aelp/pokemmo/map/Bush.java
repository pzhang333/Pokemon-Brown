package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.battle.BattleManager;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import java.util.Random;

public class Bush extends Entity {

  private final static double chance = 0.50;
  private final static Random r = new Random();

  public Bush(Location loc) {
    super(loc);
  }

  @Override
  public int getCooldown() {
    return 30 * 1000;
  }

  @Override
  public void interact(User u) {
    double d = r.nextDouble();
    if (d > chance) {
      return;
    }
    System.out.printf("%s found a pokemon in the bushes.%n", u.getUsername());
    BattleManager.getInstance().createWildBattle(u);
    u.setPath(null);
    u.setLocation(this.getLocation());
  }

}
