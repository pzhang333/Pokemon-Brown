package cs.brown.edu.aelp.pokemmo.battle.action;

import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

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
