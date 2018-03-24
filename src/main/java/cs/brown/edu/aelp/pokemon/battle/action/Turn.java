package cs.brown.edu.aelp.pokemon.battle.action;

import cs.brown.edu.aelp.pokemon.battle.Move;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public class Turn {

  public enum Action {
    NULL, RUN, SWITCH, USE_ITEM, FIGHT
  };

  private final Trainer trainer;

  private final Action action;

  public Turn(Trainer trainer, Action action) {
    this.trainer = trainer;
    this.action = action;
  }

  private Move move = null;

  public Turn(Trainer trainer, Move move) {
    this(trainer, Action.FIGHT);
    this.move = move;
  }

  public Action getAction() {
    return action;
  }

  public Trainer getTrainer() {
    return trainer;
  }

  public Move getMove() {
    return move;
  }
}
