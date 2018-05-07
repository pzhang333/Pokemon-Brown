package cs.brown.edu.aelp.pokemmo.battle;

import cs.brown.edu.aelp.pokemmo.battle.Item.ItemType;
import cs.brown.edu.aelp.pokemmo.battle.action.FightTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.ItemTurn;
import cs.brown.edu.aelp.pokemmo.battle.action.Turn;
import cs.brown.edu.aelp.pokemmo.battle.summaries.HealthChangeSummary;
import cs.brown.edu.aelp.pokemmo.battle.summaries.ItemSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemmo.pokemon.moves.Move;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;
import cs.brown.edu.aelp.util.Identifiable;

public abstract class Battle extends Identifiable {

  public enum BattleState {
    SETUP,
    WAITING,
    READY,
    DONE,
    WORKING
  }

  private final Arena arena;

  public Battle(int id, Arena arena) {
    super(id);
    this.arena = arena;
  }

  private BattleUpdate lastBattleUpdate = null;

  private BattleUpdate pendingBattleUpdate = new BattleUpdate();

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

  public BattleUpdate getLastBattleUpdate() {
    return this.lastBattleUpdate;
  }

  public BattleUpdate getPendingBattleUpdate() {
    return this.pendingBattleUpdate;
  }

  public void setLastBattleUpdate(BattleUpdate bu) {
    this.lastBattleUpdate = bu;
    this.setPendingBattleUpdate(new BattleUpdate());
  }

  public void setPendingBattleUpdate(BattleUpdate bu) {
    this.pendingBattleUpdate = bu;
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

      int speedComparison = -ft1.getTrainer().getActivePokemon()
          .getEffectiveSpeed()
          .compareTo(ft2.getTrainer().getActivePokemon().getEffectiveSpeed());

      if (speedComparison != 0) {
        return speedComparison;
      }
    }

    return 0;
  }

  protected void handleNonPokeballItem(ItemTurn turn) {
    Item item = turn.getItem();
    ItemType type = item.getType();

    if (type == ItemType.OVERLOAD) {
      Pokemon pokemon = turn.getTrainer().getActivePokemon();
      pokemon.overload();

      getPendingBattleUpdate().addSummary(new ItemSummary(item, true,
          String.format("The Overload increased %s power at the cost of health",
              pokemon.toString())));
    } else if (type == ItemType.FULL_RESTORE) {
      Pokemon pokemon = turn.getTrainer().getActivePokemon();
      pokemon.fullRestore();

      getPendingBattleUpdate()
          .addSummary(new HealthChangeSummary(pokemon, String.format(
              "%s was fully restored by a potion.", pokemon.toString())));
    } else if (type == ItemType.ETHER) {
      Pokemon pokemon = turn.getTrainer().getActivePokemon();
      for (Move m : pokemon.getMoves()) {
        m.setPP(Math.min(m.getCurrPP() + 5, m.getPP()));
      }
      getPendingBattleUpdate().addSummary(new ItemSummary(item, true,
          String.format("Ether restored %s's power.", pokemon.toString())));
    } else {
      Pokemon pokemon = turn.getTrainer().getActivePokemon();

      getPendingBattleUpdate().addSummary(new ItemSummary(item, true, String
          .format("%s used item (%d)", pokemon.toString(), item.getId())));
    }

  }

  public abstract String dbgStatus();

  public Arena getArena() {
    return arena;
  }

  // public abstract BattleUpdate sendBattleUpdate();

  public abstract void forfeit(Trainer t);

  public abstract void updateXp(Trainer winner, Trainer loser);
}
