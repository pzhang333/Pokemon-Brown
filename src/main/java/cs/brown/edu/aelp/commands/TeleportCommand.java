package cs.brown.edu.aelp.commands;

import cs.brown.edu.aelp.pokemmo.data.authentication.User;
import cs.brown.edu.aelp.pokemmo.data.authentication.UserManager;
import cs.brown.edu.aelp.pokemmo.map.Chunk;
import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemon.Main;
import java.util.Arrays;
import java.util.List;

public class TeleportCommand extends Command {

  public TeleportCommand() {
    super("teleport",
        Arrays.asList(Arrays.asList("player_id", "row", "col", "chunk_id")),
        "Teleport a player to a location", false, false);
  }

  @Override
  protected void call(List<String> args) {
    try {
      int id = Integer.parseInt(args.get(0));
      int row = Integer.parseInt(args.get(1));
      int col = Integer.parseInt(args.get(2));
      int chunkId = Integer.parseInt(args.get(3));
      User u = UserManager.getUserById(id);
      if (u == null) {
        System.out.println("ERROR: Unknown user id. Are they offline?");
        return;
      }
      Chunk c = Main.getWorld().getChunk(chunkId);
      if (c == null) {
        System.out.println("ERROR: Unknown chunk id.");
      }
      u.teleportTo(new Location(c, row, col));
      System.out.printf("Teleported %s to (%d, %d) in chunk %d%n",
          u.getUsername(), row, col, c.getId());
    } catch (NumberFormatException e) {
      System.out
          .println("ERROR: All inputs must be integers. See 'teleport help'.");
    }
  }

}
