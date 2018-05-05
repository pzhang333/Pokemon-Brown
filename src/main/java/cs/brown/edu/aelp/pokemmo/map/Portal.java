package cs.brown.edu.aelp.pokemmo.map;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemon.Main;

public class Portal extends Entity {

  private final Location goTo;

  public Portal(Location loc, Location goTo) {
    super(loc);
    this.goTo = goTo;
  }

  public Location getGoTo() {
    return this.goTo;
  }

  @Override
  public void interact(User u) {
    World w = Main.getWorld();
    if (w.getTournament() != null) {
      Tournament t = w.getTournament();
      if (t.getEntrance().equals(this)) {
        if (!t.canJoin(u)) {
          u.sendMessage(t.whyCantJoin(u));
          return;
        } else {
          t.addUser(u);
        }
      } else if (t.getExit().equals(this)) {
        t.removeUser(u);
      }
    }
    u.teleportTo(this.getGoTo());
  }

}
