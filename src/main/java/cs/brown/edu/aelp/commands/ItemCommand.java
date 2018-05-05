package cs.brown.edu.aelp.commands;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import java.util.Arrays;
import java.util.List;

public class ItemCommand extends Command {

  public ItemCommand() {
    super("item", Arrays.asList(Arrays.asList("user_id", "item_id", "amount")),
        "Set a user's amount of an item", false, false);
  }

  @Override
  protected void call(List<String> args) {
    try {
      int id = Integer.parseInt(args.get(0));
      int item = Integer.parseInt(args.get(1));
      int amt = Integer.parseInt(args.get(2));
      User u = UserManager.getUserById(id);
      if (u == null) {
        System.out.println("ERROR: Unknown user id. Are they offline?");
        return;
      }
      u.getInventory().setItemAmount(item, amt);
      System.out.printf("Set %s's amount of %d to %d%n", u.getUsername(), item,
          amt);
    } catch (NumberFormatException e) {
      System.out
          .println("ERROR: All inputs must be integers. See 'teleport help'.");
    }
  }

}
