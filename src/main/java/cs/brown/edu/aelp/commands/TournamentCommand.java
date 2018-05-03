package cs.brown.edu.aelp.commands;

import cs.brown.edu.aelp.pokemmo.map.Tournament;
import cs.brown.edu.aelp.pokemmo.map.World;
import cs.brown.edu.aelp.pokemon.Main;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TournamentCommand extends Command {

  public TournamentCommand() {
    super("tournament", Arrays.asList(Arrays.asList()),
        "Start a new tournament or end the current one", false, false);
  }

  @Override
  protected void call(List<String> args) {
    // hardcoded exit location
    World w = Main.getWorld();
    if (w.getTournament() != null) {
      w.getTournament().end();
      w.setTournament(null);
      System.out.println("Tournament forcefully ended.");
    } else {
      try {
        Tournament t = new Tournament();
        w.setTournament(t);
        System.out.println("Tournament started.");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
