package cs.brown.edu.aelp.pokemon.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.brown.edu.aelp.pokemon.battle.Turn.TurnAction;
import cs.brown.edu.aelp.pokemon.trainer.Item;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public class PvPBattle extends Battle {

  private final Trainer a;

  private final Trainer b;

  public PvPBattle(Trainer a, Trainer b) {
    this.a = a;
    this.b = b;
  }

  private Map<Trainer, Turn> turnMap = new HashMap<>();

  @Override
  public void eval() {
    if (getState() != BattleState.READY) {
      throw new RuntimeException("Battle is not in a READY state");
    }

    List<Turn> turns = new ArrayList<>(turnMap.values());
    turns.sort(this::turnComparator);

    for (Turn turn : turns) {

      if (turn.getTrainer().getActivePokemon().isKnockedOut()) {
        break;
      }

      TurnAction action = turn.getAction();

      // No action.
      if (action.equals(TurnAction.NULL)) {
        continue;
      }

      if (action.equals(TurnAction.RUN)) {
        forefit(turn.getTrainer());
        break;
      }

      if (action.equals(TurnAction.SWAP)) {
        turn.doSwap();
        continue;
      }

      if (action.equals(TurnAction.USE_ITEM)) {
        applyItem(turn.getItem());
        continue;
      }

      if (action.equals(TurnAction.FIGHT)) {
        doMove(turn.getMove());
        continue;
      }
    }
  }

  public Trainer otherTrainer(Trainer trainer) {
    if (trainer.equals(a)) {
      return b;
    }

    return a;
  }

  public void doMove(Move move) {

  }

  public void applyItem(Item item) {

  }

  public void forefit(Trainer t) {

    Trainer loser = t;

    Trainer winner;
    if (loser.equals(a)) {
      winner = b;
    } else {
      winner = a;
    }

    setLosers(Arrays.asList(loser));
    setWinners(Arrays.asList(winner));

    setState(BattleState.DONE);
  }

  @Override
  public void addTurn(Turn t) {
    turnMap.put(t.getTrainer(), t);

    if (turnMap.size() == 2) {
      setState(BattleState.READY);
    }
  }

}
