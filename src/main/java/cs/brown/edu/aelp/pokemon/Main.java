package cs.brown.edu.aelp.pokemon;

import java.util.Arrays;
import java.util.EnumSet;

import cs.brown.edu.aelp.pokemon.battle.Arena;
import cs.brown.edu.aelp.pokemon.battle.Move;
import cs.brown.edu.aelp.pokemon.battle.PokeType;
import cs.brown.edu.aelp.pokemon.battle.PokeType.PokeRawType;
import cs.brown.edu.aelp.pokemon.battle.PvPBattle;
import cs.brown.edu.aelp.pokemon.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemon.battle.action.NullTurn;
import cs.brown.edu.aelp.pokemon.battle.moves.WishMove;
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

    Move atk = new Move("atk", 1.0, 50.0, 100.0, Move.MoveCategory.SPECIAL, "",
        "", "atk", 10, 0, Move.MoveTarget.NORMAL,
        PokeType.getType(PokeRawType.NORMAL),
        EnumSet.noneOf(Move.MoveFlag.class), Move.MoveComplexity.SIMPLE);

    WishMove wish = new WishMove("id1", 1.0, 100.0, 0.0,
        Move.MoveCategory.SPECIAL, "", "", "wish", 10, 0,
        Move.MoveTarget.NORMAL, PokeType.getType(PokeRawType.NORMAL),
        EnumSet.noneOf(Move.MoveFlag.class), Move.MoveComplexity.COMPLEX);

    Pokemon p1 = new Pokemon("A", 100.0, 100.0,
        PokeType.getType(PokeRawType.NORMAL), Arrays.asList(wish, atk));

    Pokemon p2 = new Pokemon("B", 100.0, 100.0,
        PokeType.getType(PokeRawType.NORMAL), Arrays.asList(wish, atk));

    Arena arena = new Arena();

    Trainer t1 = new Trainer("1");
    t1.addPokemonToTeam(p1);

    Trainer t2 = new Trainer("2");
    t2.addPokemonToTeam(p2);

    PvPBattle battle = new PvPBattle(arena, t1, t2);

    battle.setTurn(new NullTurn(t1));
    battle.setTurn(new NullTurn(t2));
    battle.evaluate();

    System.out.println(p1);
    System.out.println(p2);

    battle.setTurn(new FightTurn(t1, atk));
    battle.setTurn(new FightTurn(t2, wish));
    battle.evaluate();

    System.out.println(p1);
    System.out.println(p2);

    battle.setTurn(new NullTurn(t1));
    battle.setTurn(new NullTurn(t2));
    battle.evaluate();

    battle.setTurn(new NullTurn(t1));
    battle.setTurn(new NullTurn(t2));
    battle.evaluate();

    System.out.println(p1);
    System.out.println(p2);

  }

}
