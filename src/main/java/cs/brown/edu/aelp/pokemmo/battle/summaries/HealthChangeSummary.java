package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class HealthChangeSummary extends BattleSummary {

  private final Pokemon pokemon;

  public HealthChangeSummary(Pokemon p, String message) {
    super(SummaryType.HEALTH_CHANGE, message);
    this.pokemon = p.snapshot();
  }

}
