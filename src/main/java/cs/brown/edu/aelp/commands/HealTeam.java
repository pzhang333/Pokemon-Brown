package cs.brown.edu.aelp.commands;

import java.util.Arrays;
import java.util.List;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;

public class HealTeam extends Command {

  public HealTeam() {
    super("heal-team", Arrays.asList(Arrays.asList("user_id")),
        "Heal all a pokemon in a user's team.", false, false);
  }

  @Override
  protected void call(List<String> args) {
    try {
      int id = Integer.parseInt(args.get(0));
      User u = UserManager.getUserById(id);
      if (u == null) {
        System.out.printf("ERROR: Unknown user id(%d). Are they offline?\n",
            id);
        return;
      }

      u.getTeam().forEach(p -> {
        p.setHealth(p.getMaxHp());
      });

      System.out.printf("Healed all of %s (id: %d)'s Pokemon\n",
          u.getUsername(), u.getId());
    } catch (NumberFormatException e) {
      System.out
          .println("ERROR: All inputs must be integers. See 'battle help'.");
    }
  }

}
