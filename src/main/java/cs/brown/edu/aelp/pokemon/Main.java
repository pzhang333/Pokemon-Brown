package cs.brown.edu.aelp.pokemon;

import cs.brown.edu.aelp.pokemon.battle.Arena;
import cs.brown.edu.aelp.pokemon.battle.PvPBattle;
import cs.brown.edu.aelp.pokemon.battle.action.Turn;
import cs.brown.edu.aelp.pokemon.battle.action.Turn.Action;
import cs.brown.edu.aelp.pokemon.trainer.Pokemon;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

/**
 * The Main class entry point.
 */
public final class Main {

  /**
   * @param args
   *          Command line arguments
   */
  public static void main(String[] args) {
    new Main().run(args);
  }

  /**
   * Private constructor for main.
   */
  private Main() {

  }

  /**
   * Entry point.
   *
   * @param args
   *          Command line arguments
   */
  private void run(String[] args) {

    Pokemon p1 = new Pokemon();
    Pokemon p2 = new Pokemon();

    Arena arena = new Arena();

    Trainer t1 = new Trainer("1");
    t1.addPokemonToTeam(p1);

    Trainer t2 = new Trainer("2");
    t2.addPokemonToTeam(p2);

    PvPBattle battle = new PvPBattle(arena, t1, t2);

    battle.setTurn(new Turn(t1, Action.RUN));
    battle.setTurn(new Turn(t2, Action.NULL));

    battle.evaluate();
  }

}
