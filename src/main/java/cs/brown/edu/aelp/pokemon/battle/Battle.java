package cs.brown.edu.aelp.pokemon.battle;

import cs.brown.edu.aelp.pokemon.battle.action.Turn;
import cs.brown.edu.aelp.pokemon.battle.action.Turn.Action;

public class Battle {

  public enum BattleState {
    SETUP, WAITING, READY, DONE
  };

  private BattleState battleState = BattleState.SETUP;

  /**
   * @return the battleState
   */
  public BattleState getBattleState() {
    return battleState;
  }

  /**
   * @param battleState
   *          the battleState to set
   */
  protected void setBattleState(BattleState battleState) {
    this.battleState = battleState;
  }

  protected int turnComparator(Turn t1, Turn t2) {

    int actionComparison = t1.getAction().compareTo(t2.getAction());

    if (actionComparison != 0) {
      return actionComparison;
    }

    if (t1.getAction().equals(Action.FIGHT)) {

      int priorityComparison = -t1.getMove().getPriority()
          .compareTo(t2.getMove().getPriority());

      if (priorityComparison != 0) {
        return priorityComparison;
      }

      int speedComparison = -t1.getTrainer().getActivePokemon().getSpeed()
          .compareTo(t2.getTrainer().getActivePokemon().getSpeed());

      if (speedComparison != 0) {
        return speedComparison;
      }
    }

    return 0;
  }
}
