package cs.brown.edu.aelp.pokemmo.battle.summaries;

import cs.brown.edu.aelp.pokemmo.battle.BattleSummary;
import cs.brown.edu.aelp.pokemmo.pokemon.Pokemon;

public class SwitchSummary extends BattleSummary {

  private final Pokemon pokemonIn;

  private final Pokemon pokemonOut;

  public SwitchSummary(Pokemon pokeIn, Pokemon pokeOut) {
    this(pokeIn, pokeOut, String.format("%s swapped in for %s.",
        pokeIn.toString(), pokeOut.toString()));
  }

  public SwitchSummary(Pokemon pokeIn, Pokemon pokeOut, String message) {
    super(SummaryType.SWITCH, message);
    this.pokemonIn = pokeIn.snapshot();
    this.pokemonOut = pokeOut.snapshot();
  }

}
