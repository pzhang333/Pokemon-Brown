package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.Turn;

public class Battle {

  private Arena arena;

  public Battle(Arena arena) {
    this.arena = arena;
  }

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

    if (t1 instanceof FightTurn && t2 instanceof FightTurn) {

      FightTurn ft1 = (FightTurn) t1;
      FightTurn ft2 = (FightTurn) t2;

      int priorityComparison = -ft1.getMove().getPriority()
          .compareTo(ft2.getMove().getPriority());

      if (priorityComparison != 0) {
        return priorityComparison;
      }

      int speedComparison = -ft1.getTrainer().getActivePokemon().getSpeed()
          .compareTo(ft2.getTrainer().getActivePokemon().getSpeed());

      if (speedComparison != 0) {
        return speedComparison;
      }
    }

    return 0;
  }

  public Arena getArena() {
    return arena;
  }
}
