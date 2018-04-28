package cs.brown.edu.aelp.commands;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import java.util.Arrays;
import java.util.List;

public class CoinsCommand extends Command {

  public CoinsCommand() {
    super("coins", Arrays.asList(Arrays.asList("user_id", "amount")),
        "Set a user's number of coins", false, false);
  }

  @Override
  protected void call(List<String> args) {
    try {
      int id = Integer.parseInt(args.get(0));
      int coins = Integer.parseInt(args.get(1));
      User u = UserManager.getUserById(id);
      if (u == null) {
        System.out.println("ERROR: Unknown user id. Are they offline?");
        return;
      }
      u.setCurrency(coins);
      System.out.printf("Set %s's coins to %d%n", u.getUsername(), coins);
    } catch (NumberFormatException e) {
      System.out
          .println("ERROR: All inputs must be integers. See 'teleport help'.");
    }
  }

}
