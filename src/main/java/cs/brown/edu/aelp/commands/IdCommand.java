package cs.brown.edu.aelp.commands;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import java.util.Arrays;
import java.util.List;

public class IdCommand extends Command {

  public IdCommand() {
    super("id", Arrays.asList(Arrays.asList("username")),
        "Get a player's ID from their (optionally partial) username. Must be online.",
        false, false);
  }

  @Override
  protected void call(List<String> args) {
    String username = args.get(0);
    User u = null;
    for (User check : UserManager.getAllUsers()) {
      if (check.getUsername().equalsIgnoreCase(username)) {
        u = check;
        break;
      }
    }
    if (u == null) {
      System.out.printf("No online users have that name.%n");
      return;
    }
    System.out.printf("%s's id: %d%n", u.getUsername(), u.getId());
  }

}
