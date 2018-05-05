package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class FightSummary extends BattleSummary {

  private final Pokemon attacking;

  private final Pokemon defending;

  private final String animation;

  public FightSummary(Pokemon attacking, Pokemon defending, String message,
      String animation) {
    super(SummaryType.FIGHT, message);

    this.attacking = attacking.snapshot();
    this.defending = defending.snapshot();
    this.animation = animation;
  }

}
