package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.battle.Item;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class ItemSummary extends BattleSummary {

  private Item item;

  private boolean success;

  private String animation;

  private Pokemon target;

  public ItemSummary(Item item, boolean success, String message) {
    this(item, success, null, message);
  }

  public ItemSummary(Item item, boolean success, Pokemon target,
      String message) {
    this(item, success, target, message, "");
  }

  public ItemSummary(Item item, boolean success, Pokemon target, String message,
      String animation) {
    super(SummaryType.ITEM, message);

    this.animation = animation;
    this.success = success;
    this.item = item;
    this.target = target;
  }

}
