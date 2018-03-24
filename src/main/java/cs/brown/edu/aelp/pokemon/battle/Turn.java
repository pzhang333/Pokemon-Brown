package cs.brown.edu.aelp.pokemon.battle;

import cs.brown.edu.aelp.pokemon.Pokemon;
import cs.brown.edu.aelp.pokemon.trainer.Item;
import cs.brown.edu.aelp.pokemon.trainer.Trainer;

public class Turn {

  private Integer customPriority = Integer.MAX_VALUE;

  private final Trainer trainer;

  private final TurnAction action;

  public Turn(Trainer trainer, TurnAction action) {
    this.trainer = trainer;
    this.action = action;
  }

  public enum TurnAction {
    FIGHT, USE_ITEM, SWAP, RUN, NULL
  }

  /* Null */

  /* Run */

  /* Swap */
  private Pokemon sub = null;

  public void setSwap(Pokemon sub) {
    if (action != TurnAction.SWAP) {
      throw new IllegalAccessError("Cannot setSwap() for a non-SWAP turn");
    }

    this.sub = sub;
  }

  public void doSwap() {

  }

  /* Use Item */
  private Item item = null;

  public boolean setUseItem(Item item) {
    if (action != TurnAction.USE_ITEM) {
      throw new IllegalAccessError(
          "Cannot setUseItem() for a non-USE_ITEM turn");
    }

    if (!getTrainer().getInventory().hasItem(item)) {
      return false;
    }

    this.item = item;

    return true;
  }

  /* Fight */
  private Move move = null;

  public boolean setFight(Move move) {
    if (action != TurnAction.FIGHT) {
      throw new IllegalAccessError("Cannot setFight() for a non-Fight turn");
    }

    if (!getTrainer().getActivePokemon().hasMove(move)) {
      return false;
    }

    this.move = move;

    return true;
  }

  /**
   * @return the customPriority
   */
  public Integer getCustomPriority() {
    return customPriority;
  }

  /**
   * @param customPriority
   *          the customPriority to set
   */
  public void setCustomPriority(Integer customPriority) {
    this.customPriority = customPriority;
  }

  /**
   * @return the trainer
   */
  public Trainer getTrainer() {
    return trainer;
  }

  /**
   * @return the action
   */
  public TurnAction getAction() {
    return action;
  }

  /**
   * @return the sub
   */
  public Pokemon getSub() {
    return sub;
  }

  /**
   * @return the item
   */
  public Item getItem() {
    return item;
  }

  /**
   * @return the move
   */
  public Move getMove() {
    return move;
  }
}
