package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.Turn;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.util.Identifiable;

public abstract class Battle extends Identifiable {

  public enum BattleState {
    SETUP,
    WAITING,
    READY,
    DONE
  };

  public enum BattleType {
    WILD_BATTLE,
    PVP_BATTLE
  };

  private final Arena arena;
  private final Trainer trainer1;
  private final Trainer trainer2;

  public Battle(int id, Arena arena, Trainer t1, Trainer t2) {
    super(id);
    this.arena = arena;
    this.trainer1 = t1;
    this.trainer2 = t2;
  }

  public Trainer getTrainer1() {
    return this.trainer1;
  }

  public Trainer getTrainer2() {
    return this.trainer2;
  }

  private BattleState battleState = BattleState.SETUP;

  public abstract BattleType getBattleType();

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
