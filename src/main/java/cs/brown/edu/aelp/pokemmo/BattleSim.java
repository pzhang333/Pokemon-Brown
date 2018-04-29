package cs.brown.edu.aelp.pokemmo;

import cs.brown.edu.aelp.pokemmo.battle.Arena;
import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.NullTurn;
import cs.brown.edu.aelp.pokemmo.battle.impl.PvPBattle;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.PokemonLoader;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.MoveLoader;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.complex.Wish;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

/**
 * The Main class entry point.
 */
public final class BattleSim {

  /**
   * @param args
   *          Command line arguments
   */
  /*
   * public static void main(String[] args) { new BattleSim().run(args); }
   */

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

  // This code is depreciated.

  public static void main(String[] args) {

    Pokemon p1 = PokemonLoader.load("bulbasaur", Pokemon.calcXpByLevel(50));
    Pokemon p2 = PokemonLoader.load("pikachu", Pokemon.calcXpByLevel(50));
    Pokemon p3 = PokemonLoader.load("pichu", Pokemon.calcXpByLevel(50));

    Move atk = MoveLoader.getMoveById(33);

    MoveLoader.addOverride(new Wish());
    Move wish = MoveLoader.getMoveById(273);

    Arena arena = new Arena();

    System.out.println(p1.getMaxHp());
    System.out.println(p2.getMaxHp());
    System.out.println(p3.getMaxHp());

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

    System.out.println(p1);
    System.out.println(p2);

    if (true) {
      return;
    }

    /*
     * while (battle.getBattleState().equals(BattleState.WAITING)) {
     * System.out.println(t1.getActivePokemon());
     * System.out.println(t2.getActivePokemon());
     * 
     * battle.setTurn(new FightTurn(t1, atk)); battle.setTurn(new NullTurn(t2));
     * 
     * if (battle.getBattleState().equals(BattleState.READY)) {
     * battle.evaluate(); } else { break; }
     * 
     * System.out.println(t1.getActivePokemon());
     * System.out.println(t2.getActivePokemon()); }
     * 
     * battle.setTurn(new FightTurn(t1, atk)); battle.setTurn(new SwitchTurn(t2,
     * p3, t2.getActivePokemon())); battle.evaluate();
     * 
     * while (battle.getBattleState().equals(BattleState.WAITING)) {
     * 
     * System.out.println(t1.getActivePokemon());
     * System.out.println(t2.getActivePokemon());
     * 
     * battle.setTurn(new FightTurn(t1, atk)); battle.setTurn(new NullTurn(t2));
     * battle.evaluate();
     * 
     * System.out.println(t1.getActivePokemon());
     * System.out.println(t2.getActivePokemon()); }
     */
  }
}
