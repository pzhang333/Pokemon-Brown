package cs.brown.edu.aelp.pokemon.battle;

import java.util.List;
import java.util.PriorityQueue;

import cs.brown.edu.aelp.pokemon.battle.Turn.TurnAction;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public abstract class Battle {

  public enum BattleState {
    WAITING, READY, DONE
  };

  private BattleState state = BattleState.WAITING;

  private List<Trainer> winners = null;

  private List<Trainer> losers = null;

  private Turn activeTurn = null;

  private PriorityQueue<BattleEventListener> eventListeners;

  protected Battle() {
    eventListeners = new PriorityQueue<>(
        (BattleEventListener bel1, BattleEventListener bel2) -> {
          return bel1.getPriority().compareTo(bel2.getPriority());
        });
  }

  public abstract void eval();

  public abstract void addTurn(Turn t);

  public BattleState getState() {
    return state;
  }

  public void setState(BattleState state) {
    this.state = state;
  }

  public void registerBattleEventListener(BattleEventListener listener) {
    eventListeners.add(listener);
  }

  public boolean isRegistered(BattleEventListener listener) {
    return eventListeners.contains(listener);
  }

  public boolean deregisterBattleEventListener(BattleEventListener listener) {
    return eventListeners.remove(listener);
  }

  /**
   * @return the winners
   */
  public List<Trainer> getWinners() {
    return winners;
  }

  /**
   * @param winners
   *          the winners to set
   */
  public void setWinners(List<Trainer> winners) {
    this.winners = winners;
  }

  /**
   * @return the losers
   */
  public List<Trainer> getLosers() {
    return losers;
  }

  /**
   * @param losers
   *          the losers to set
   */
  public void setLosers(List<Trainer> losers) {
    this.losers = losers;
  }

  public Turn getActiveTurn() {
    return activeTurn;
  }

  protected void setActiveTurn(Turn turn) {
    activeTurn = turn;
  }

  protected int turnComparator(Turn t1, Turn t2) {
    int actionCompare = t1.getAction().compareTo(t2.getAction());

    if (actionCompare != 0) {
      return actionCompare;
    }

    if (t1.getAction().equals(TurnAction.FIGHT)) {
      int prioCompare = -t1.getMove().getPriority()
          .compareTo(t2.getMove().getPriority());

      if (prioCompare != 0) {
        return prioCompare;
      }

      int speedCompare = t1.getTrainer().getActivePokemon().getSpeed()
          .compareTo(t2.getTrainer().getActivePokemon().getSpeed());

      if (speedCompare != 0) {
        return speedCompare;
      }
    }

    return 0;
  }
}
