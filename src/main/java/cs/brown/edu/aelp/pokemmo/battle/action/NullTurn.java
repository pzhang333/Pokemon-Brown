package cs.brown.edu.aelp.pokemmo.battle.action;

import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class NullTurn extends Turn {

  public NullTurn(Trainer trainer) {
    super(trainer, Action.NULL);
  }

}
