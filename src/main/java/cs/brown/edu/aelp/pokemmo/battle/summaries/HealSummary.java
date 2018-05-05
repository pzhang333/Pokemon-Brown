package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class HealSummary extends BattleSummary {

  private final int amount;
  private final Pokemon pokemon;

  public HealSummary(Pokemon p, int amount, String message) {
    super(SummaryType.HEAL, message);
    this.amount = amount;
    this.pokemon = p;
  }

}
