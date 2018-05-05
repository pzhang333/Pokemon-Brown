package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.battle.Item;

public class ItemSummary extends BattleSummary {

  private Item item;

  private boolean success;

  private String animation;

  public ItemSummary(Item item, boolean success, String message) {
    this(item, success, message, "");
  }

  public ItemSummary(Item item, boolean success, String message,
      String animation) {
    super(SummaryType.ITEM, message);

    this.animation = animation;
    this.success = success;
  }

}
