package cs.brown.edu.aelp.pokemmo.battle.action;

import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public abstract class Turn {

  public enum Action {
    NULL, RUN, SWITCH, USE_ITEM, FIGHT
  };

  private final Trainer trainer;

  private final Action action;

  public Turn(Trainer trainer, Action action) {
    this.trainer = trainer;
    this.action = action;
  }

  public Trainer getTrainer() {
    return trainer;
  }

  public Action getAction() {
    return action;
  }
}
