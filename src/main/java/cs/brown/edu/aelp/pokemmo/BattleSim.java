package cs.brown.edu.aelp.pokemmo;

import java.util.Arrays;
import java.util.EnumSet;

import cs.brown.edu.aelp.pokemmo.battle.Arena;
import cs.brown.edu.aelp.pokemmo.battle.Battle.BattleState;
import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.NullTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.SwitchTurn;
import cs.brown.edu.aelp.pokemmo.battle.impl.PvPBattle;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeType;
import cs.brown.edu.aelp.pokemmo.pokemon.PokeType.PokeRawType;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.WishMove;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

/**
 * The Main class entry point.
 */
public final class BattleSim {

  /**
   * @param args
   *          Command line arguments
   */
  public static void main(String[] args) {
    new BattleSim().run(args);
  }

  /**
   * Private constructor for main.
   */
  private BattleSim() {

  }

  /**
   * Entry point.
   *
   * @param args
   *          Command line arguments
   */
  private void run(String[] args) {

    Move atk = new Move("atk", 1, .9, 100.0, Move.MoveCategory.SPECIAL, "", "",
        "atk", 10, 0, Move.MoveTarget.NORMAL,
        PokeType.getType(PokeRawType.NORMAL),
        EnumSet.noneOf(Move.MoveFlag.class), Move.MoveComplexity.SIMPLE);

    WishMove wish = new WishMove("id1", 2, 1.0, 0.0, Move.MoveCategory.SPECIAL,
        "", "", "wish", 10, 0, Move.MoveTarget.NORMAL,
        PokeType.getType(PokeRawType.NORMAL),
        EnumSet.noneOf(Move.MoveFlag.class), Move.MoveComplexity.COMPLEX);

    Pokemon p1 = new Pokemon(1, "Slaking", 150, 150, 160, 100, 95, 65, 100,
        Pokemon.calcXpByLevel(30), PokeType.getType(PokeRawType.NORMAL),
        Arrays.asList(atk));

    Pokemon p2 = new Pokemon(2, "Chauncy", 150, 150, 160, 100, 95, 65, 100,
        Pokemon.calcXpByLevel(30), PokeType.getType(PokeRawType.NORMAL),
        Arrays.asList(atk, wish));

    Pokemon p3 = new Pokemon(3, "Pikachu", 150, 150, 160, 100, 95, 65, 100,
        Pokemon.calcXpByLevel(30), PokeType.getType(PokeRawType.ELECTRIC),
        Arrays.asList(atk, wish));

    Arena arena = new Arena();

    Trainer t1 = new Trainer(1);
    t1.addPokemonToTeam(p1);

    Trainer t2 = new Trainer(2);
    t2.addPokemonToTeam(p2);
    t2.addPokemonToTeam(p3);

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

    while (battle.getBattleState().equals(BattleState.WAITING)) {
      System.out.println(t1.getActivePokemon());
      System.out.println(t2.getActivePokemon());

      battle.setTurn(new FightTurn(t1, atk));
      battle.setTurn(new NullTurn(t2));

      if (battle.getBattleState().equals(BattleState.READY)) {
        battle.evaluate();
      } else {
        break;
      }

      System.out.println(t1.getActivePokemon());
      System.out.println(t2.getActivePokemon());
    }

    battle.setTurn(new FightTurn(t1, atk));
    battle.setTurn(new SwitchTurn(t2, p3, t2.getActivePokemon()));
    battle.evaluate();

    while (battle.getBattleState().equals(BattleState.WAITING)) {

      System.out.println(t1.getActivePokemon());
      System.out.println(t2.getActivePokemon());

      battle.setTurn(new FightTurn(t1, atk));
      battle.setTurn(new NullTurn(t2));
      battle.evaluate();

      System.out.println(t1.getActivePokemon());
      System.out.println(t2.getActivePokemon());
    }
  }
}
