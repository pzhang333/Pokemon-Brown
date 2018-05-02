package cs.brown.edu.aelp.commands;

import java.util.Arrays;
import java.util.List;

import cs.brown.edu.aelp.pokemmo.battle.BattleManager;
import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;

public class BattleCommand extends Command {

  public BattleCommand() {
    super("battle", Arrays.asList(Arrays.asList("user1_id", "user2_id")),
        "Start a battle between two users.", false, false);
  }

  @Override
  protected void call(List<String> args) {
    try {
      int id1 = Integer.parseInt(args.get(0));
      int id2 = Integer.parseInt(args.get(1));

      User u1 = UserManager.getUserById(id1);
      if (u1 == null) {
        System.out.printf("ERROR: Unknown user id(%d). Are they offline?\n",
            id1);
        return;
      }

      User u2 = UserManager.getUserById(id2);
      if (u2 == null) {
        System.out.printf("ERROR: Unknown user id(%d). Are they offline?\n",
            id2);
        return;
      }

      BattleManager.getInstance().createPvPBattle(u1, u2);

      System.out.printf(
          "Started battle between users: %s (id: %d) and %s (id: %d)\n",
          u1.getUsername(), u1.getId(), u2.getUsername(), u2.getId());
    } catch (NumberFormatException e) {
      System.out
          .println("ERROR: All inputs must be integers. See 'battle help'.");
    }
  }

}
