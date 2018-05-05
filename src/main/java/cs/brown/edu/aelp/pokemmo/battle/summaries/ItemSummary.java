package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.battle.Item;

public class ItemSummary extends BattleSummary {

  private Item item;

  private String animation;

  public ItemSummary(Item item, String message) {
    this(item, message, "");
  }

  public ItemSummary(Item item, String message, String animation) {
    super(SummaryType.ITEM, message);

    this.animation = animation;
  }

}
