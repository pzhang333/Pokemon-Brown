package cs.brown.edu.aelp.commands;

import cs.brown.edu.aelp.pokemmo.map.Location;
import cs.brown.edu.aelp.pokemmo.map.Portal;
import cs.brown.edu.aelp.pokemmo.map.Tournament;
import cs.brown.edu.aelp.pokemmo.map.World;
import cs.brown.edu.aelp.pokemon.Main;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TournamentCommand extends Command {

  public TournamentCommand() {
    super("tournament", Arrays.asList(),
        "Start a new tournament or end the current one", false, false);
  }

  @Override
  protected void call(List<String> args) {
    // hardcoded exit location
    World w = Main.getWorld();
    Location entrance = new Location(w.getChunk(1), 5, 0);
    Location exit = new Location(w.getChunk(1), 5, 5);
    if (w.getTournament() != null) {
      w.getTournament().end();
      w.setTournament(null);
      System.out.println("Tournament forcefully ended.");
    } else {
      try {
        Tournament t = new Tournament(4, 100, exit);
        Portal p = new Portal(entrance, new Location(t.getChunk(), 1, 1));
        entrance.getChunk().addEntity(p);
        t.setPortal(p);
        w.setTournament(t);
        System.out.println(
            "Tournament started. 4 players, 100 coin buy-in, portal at (5,0) in chunk 1.");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
