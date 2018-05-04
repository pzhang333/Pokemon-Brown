package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.Turn;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.util.Identifiable;

public abstract class Battle extends Identifiable {

  public enum BattleState {
    SETUP, WAITING, READY, DONE, WORKING
  }

  private final Arena arena;

  public Battle(int id, Arena arena) {
    super(id);
    this.arena = arena;
  }

  private BattleState battleState = BattleState.SETUP;

  public abstract Trainer getLoser();

  public abstract Trainer getWinner();

  /**
   * @return the battleState
   */
  public synchronized BattleState getBattleState() {
    return battleState;
  }

  /**
   * @param battleState
   *          the battleState to set
   */
  protected synchronized void setBattleState(BattleState battleState) {
    this.battleState = battleState;
  }

  public abstract void evaluate();

  public abstract boolean setTurn(Turn t);

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

      int speedComparison = -ft1.getTrainer().getActivePokemon().getEffectiveSpeed()
          .compareTo(ft2.getTrainer().getActivePokemon().getEffectiveSpeed());

      if (speedComparison != 0) {
        return speedComparison;
      }
    }

    return 0;
  }

  public abstract String dbgStatus();

  public Arena getArena() {
    return arena;
  }

  // public abstract BattleUpdate sendBattleUpdate();

  public abstract void forfeit(Trainer t);
}
