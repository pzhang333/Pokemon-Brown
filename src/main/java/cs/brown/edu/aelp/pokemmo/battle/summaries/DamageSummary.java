package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class DamageSummary extends BattleSummary {

  private final int amount;
  private final Pokemon pokemon;

  public DamageSummary(Pokemon p, int amount, String message) {
    super(SummaryType.DAMAGE, message);
    this.amount = amount;
    this.pokemon = p;
  }

}
