package cs.brown.edu.aelp.commands;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import java.util.Arrays;
import java.util.List;

public class KickCommand extends Command {

  public KickCommand() {
    super("kick", Arrays.asList(Arrays.asList("user_id")),
        "Kick a player (and remove them from memory, "
            + "effectively rolling their character back to the previous savepoint).",
        false, false);
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
      u.kick();
      UserManager.forgetUser(u);

      System.out.printf("Kicked %s (%d).\n", u.getUsername(), u.getId());
    } catch (NumberFormatException e) {
      System.out
          .println("ERROR: All inputs must be integers. See 'kick help'.");
    }
  }

}
