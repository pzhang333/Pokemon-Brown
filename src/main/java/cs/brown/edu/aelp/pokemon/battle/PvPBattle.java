package cs.brown.edu.aelp.pokemon.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.brown.edu.aelp.pokemon.battle.action.Turn;
import cs.brown.edu.aelp.pokemon.battle.action.Turn.Action;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public class PvPBattle extends Battle {

  private final Arena arena;

  private final Trainer a;

  private final Trainer b;

  private Map<Trainer, Turn> turnsMap = new HashMap<>();

  public PvPBattle(Arena arena, Trainer a, Trainer b) {
    this.arena = arena;
    this.a = a;
    this.b = b;

    setBattleState(BattleState.WAITING);
  }

  public void evaluate() {
    if (!getBattleState().equals(BattleState.READY)) {
      throw new RuntimeException("Not in ready state!");
    }

    List<Turn> turns = new ArrayList<>(turnsMap.values());
    turns.sort(this::turnComparator);

    boolean stop = false;
    for (Turn turn : turns) {

      if (stop) {
        break;
      }

      if (turn.getAction().equals(Action.NULL)) {
        continue;
      }

      if (turn.getAction().equals(Action.RUN)) {
        victory(other(turn.getTrainer()));
      }
    }

    turnsMap.clear();
  }

  public void victory(Trainer t) {
    System.out.println("Victory for: " + t.getId());

    setBattleState(BattleState.DONE);
  }

  public void setTurn(Turn t) {

    if (!getBattleState().equals(BattleState.WAITING)) {
      throw new RuntimeException("Not in waiting state!");
    }

    turnsMap.put(t.getTrainer(), t);

    if (turnsMap.size() == 2) {
      setBattleState(BattleState.READY);
    }
  }

  private Trainer other(Trainer t) {
    if (t.equals(a)) {
      return b;
    }

    return a;
  }
}
