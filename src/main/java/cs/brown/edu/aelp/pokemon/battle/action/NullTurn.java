package cs.brown.edu.aelp.pokemon.battle.action;

import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public class NullTurn extends Turn {

  public NullTurn(Trainer trainer) {
    super(trainer, Action.NULL);
  }

}
