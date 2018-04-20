package cs.brown.edu.aelp.pokemmo.battle.action;

import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class SwitchTurn extends Turn {

  private final Pokemon in;

  private final Pokemon out;

  public SwitchTurn(Trainer trainer, Pokemon in, Pokemon out) {
    super(trainer, Action.SWITCH);

    this.in = in;
    this.out = out;
  }

  public Pokemon getPokemonIn() {
    return in;
  }

  public Pokemon getPokemonOut() {
    return out;
  }

}
