package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class HealSummary extends BattleSummary {

  private final int amount;

  public HealSummary(Pokemon p, String message, int amount) {
    super(SummaryType.HEAL, message);
    this.amount = amount;
  }

}
