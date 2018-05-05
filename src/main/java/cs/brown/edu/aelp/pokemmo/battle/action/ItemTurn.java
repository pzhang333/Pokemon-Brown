package cs.brown.edu.aelp.pokemmo.battle.action;

import cs.brown.edu.aelp.pokemmo.battle.Item;
import cs.brown.edu.aelp.pokemmo.trainer.Trainer;

public class ItemTurn extends Turn {

  private final Item item;

  public ItemTurn(Trainer trainer, Item item) {
    super(trainer, Turn.Action.USE_ITEM);

    this.item = item;
  }

  public Item getItem() {
    return item;
  }
}
