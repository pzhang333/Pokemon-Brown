package cs.brown.edu.aelp.pokemon.battle.action;

import cs.brown.edu.aelp.pokemon.battle.Move;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public class FightTurn extends Turn {

  private final Move move;

  public FightTurn(Trainer trainer, Move move) {
    super(trainer, Action.FIGHT);

    this.move = move;
  }

  public Move getMove() {
    return move;
  }
}
